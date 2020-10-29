package ru.yusdm.training.common.sleuth

import brave.baggage.BaggageField
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Parameter

class Person {}

@Configuration
class Zu {
    @Bean
    fun person() = Person()
}


@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CreateBaggage(val key: String, val value: String, val excludeFromMdcAfterMethodExit: Boolean = true)

/*
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = ["app.sleuth.enabled"], havingValue = "true", matchIfMissing = false)
@ConditionalOnBean(value = [TraceAutoConfiguration::class])
class SleuthAutoConfig {
}*/

object SleuthBaggageFieldMaintainer {
    fun setBaggageField(key: String, value: String) {
        BaggageField.create(key).also {
            val wasUpdated = it.updateValue(value)
            if (wasUpdated) {
                MDC.put(key, value)
            }
        }
    }

    fun getBaggageField(key: String): String {
        return BaggageField.getByName(key)?.value ?: ""
    }
}
const val ANNOTATION ="ru.yusdm.training.common.sleuth.CreateBaggage"
@Aspect
@Component
//@ConditionalOnProperty(value = ["app.sleuth.enabled"], havingValue = "true", matchIfMissing = false)
@ConditionalOnBean(value = [TraceAutoConfiguration::class])
class CreateBaggageAspect(beanFactory: BeanFactory) {
    private val parser = SpelExpressionParser()

    private val beanEvaluationContext = StandardEvaluationContext().also {
        it.setBeanResolver(BeanFactoryResolver(beanFactory))
    }

    @Around("execution(public * *(.., @ru.yusdm.training.common.sleuth.CreateBaggage (*), ..))")
    fun applyBaggageForAnnotatedArgument(joinPoint: ProceedingJoinPoint): Any? {
        val annotationWithValue: Pair<CreateBaggage, Any>? = getAnnotationAndValue(joinPoint)

        if (annotationWithValue != null) {
            val (annotation, value) = annotationWithValue
            val expression = annotation.value

            val context = StandardEvaluationContext().also {
                val splited: List<String> = expression.replaceFirst("#", "").split('.')
                it.setVariable(splited.first(), value)
            }

            var mdcValueWasAssigned = false
            val evaluatedValue = parser.parseExpression(expression).getValue(context, String::class.java)
            if (evaluatedValue != null) {
                MDC.put(
                    annotation.key,
                    evaluatedValue
                )
                mdcValueWasAssigned = true
            }
            val proceed = joinPoint.proceed()
            if (mdcValueWasAssigned && annotation.excludeFromMdcAfterMethodExit) {
                MDC.remove(annotation.key)
            }

            return proceed
        } else {
            return joinPoint.proceed()
        }
    }

    @Around("@annotation(ru.yusdm.training.common.sleuth.CreateBaggage)")
    fun applyBaggageForAnnotatedMethod(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val createBaggageAnnotation = signature.method.getAnnotation(CreateBaggage::class.java)

        var mdcValueWasAssigned = false
        if (createBaggageAnnotation.value.isNotBlank()) {
            val evaluatedValue = getEvaluatedExpressionValueTakenFromBean(createBaggageAnnotation.value)

            if (evaluatedValue != null) {
                MDC.put(
                    createBaggageAnnotation.key,
                    evaluatedValue
                )
                mdcValueWasAssigned = true
            }
        }

        val proceed = joinPoint.proceed()

        if (mdcValueWasAssigned && createBaggageAnnotation.excludeFromMdcAfterMethodExit) {
            MDC.remove(createBaggageAnnotation.key)
        }

        return proceed
    }

    private fun getEvaluatedExpressionValueTakenFromBean(expression: String): String? {
        val e = parser.parseExpression(expression)
        return e.getValue(beanEvaluationContext, String::class.java)
    }

    private fun getAnnotationAndValue(joinPoint: ProceedingJoinPoint): Pair<CreateBaggage, Any>? {

        val signature = joinPoint.signature as MethodSignature
        val parameters: Array<Parameter> = signature.method.parameters

        val value: Any?
        for (i in 0..parameters.size) {
            if (parameters[i].isAnnotationPresent(CreateBaggage::class.java)) {
                value = joinPoint.args[i]
                val annotation = parameters[i].getAnnotation(CreateBaggage::class.java)

                return annotation to value
            }
        }

        return null
    }

}