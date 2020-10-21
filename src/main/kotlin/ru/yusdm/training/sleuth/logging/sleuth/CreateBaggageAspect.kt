package ru.yusdm.training.sleuth.logging.sleuth

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.beans.factory.BeanFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Parameter

@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CreateBaggage(val key: String, val value: String, val excludeFromMdcAfterMethodExit: Boolean = true)

@Aspect
@Component
class CreateBaggageAspect(beanFactory: BeanFactory) {
    private val parser = SpelExpressionParser()

    private val beanEvaluationContext = StandardEvaluationContext().also {
        it.setBeanResolver(BeanFactoryResolver(beanFactory))
    }

    @Around("execution(public * *(.., @ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage (*), ..))")
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

    @Around("@annotation(ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage)")
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

        var value: Any? = null
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