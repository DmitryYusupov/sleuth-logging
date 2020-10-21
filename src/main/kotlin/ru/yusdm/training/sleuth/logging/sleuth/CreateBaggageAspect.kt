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

  //  @Around("@annotation(ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage)")
    @Around("execution(public * *(.., @ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage (*), ..))")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val createBaggageAnnotation = signature.method.getAnnotation(CreateBaggage::class.java)

        var mdcValueWasAssigned = false
        if (createBaggageAnnotation.value.isNotBlank()) {
            val evaluatedValue = getEvaluatedExpressionValue(
                expression = createBaggageAnnotation.value, joinPoint = joinPoint
            )
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

    private fun getEvaluatedExpressionValue(expression: String, joinPoint: ProceedingJoinPoint): String? {
        return when (getSpelResolverType(expression)) {
            SpelResolverType.BEAN -> getEvaluatedExpressionValueTakenFromBean(expression)
            SpelResolverType.ARGUMENT -> getEvaluatedExpressionValueTakenFromArguments(expression, joinPoint)
        }
    }

    private enum class SpelResolverType {
        BEAN, ARGUMENT
    }

    private fun getSpelResolverType(expression: String): SpelResolverType {
        return if (expression.first() == '@') {
            SpelResolverType.BEAN
        } else {
            SpelResolverType.ARGUMENT
        }
    }

    private fun getEvaluatedExpressionValueTakenFromBean(expression: String): String? {
        val e = parser.parseExpression(expression)
        return e.getValue(beanEvaluationContext, String::class.java)
    }

    private fun getEvaluatedExpressionValueTakenFromArguments(
        expression: String,
        joinPoint: ProceedingJoinPoint
    ): String? {

        fun getAnnotatedArgumentValue(): Any? {
            val signature = joinPoint.signature as MethodSignature
            val parameters: Array<Parameter> = signature.method.parameters

            var value: Any? = null
            for (i in 0..parameters.size) {
                if (parameters[i].isAnnotationPresent(CreateBaggage::class.java)) {
                    value = joinPoint.args[i]
                    break
                }
            }

            return value
        }

        return getAnnotatedArgumentValue()?.let { argValue ->
            val context = StandardEvaluationContext().also {
                val splited: List<String> = expression.replaceFirst("#", "").split('.')
                it.setVariable(splited.first(), argValue)
            }
            val exp = parser.parseExpression(expression)

            exp.getValue(context, String::class.java)
        }
    }

}