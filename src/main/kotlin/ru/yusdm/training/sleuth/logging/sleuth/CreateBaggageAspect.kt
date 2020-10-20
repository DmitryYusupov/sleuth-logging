package ru.yusdm.training.sleuth.logging.sleuth

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import ru.yusdm.training.sleuth.logging.model.User


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CreateBaggage(val key: String, val value: String, val excludeFromMdcAfterMethodExit: Boolean = true)

@Aspect
@Component
class CreateBaggageAspect(beanFactory: BeanFactory) {

    private val parser = SpelExpressionParser()
    private val context = StandardEvaluationContext().also {
        it.setBeanResolver(BeanFactoryResolver(beanFactory))
    }

    @Around("@annotation(ru.yusdm.training.sleuth.logging.sleuth.CreateBaggage)")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val createBaggageAnnotation = signature.method.getAnnotation(CreateBaggage::class.java)

        with(createBaggageAnnotation){
            if (this.value.isNotBlank()){
                val value = parser.parseExpression(this.value).getValue(User("name"), String::class.java)
                MDC.put(this.key, this.value)
            }
        }

        val proceed = joinPoint.proceed()

        with(createBaggageAnnotation){
            if (this.excludeFromMdcAfterMethodExit){
                MDC.remove(this.key)
            }
        }


        /*val context: tandardEvaluationContext = StandardEvaluationContext()
        context.setBeanResolver(BeanFactoryResolver(this.beanFactory))
        val expression = parser.parseExpression("@someOtherBean.getData()")
// or "@someOtherBean.data"
// or "@someOtherBean.data"
        val value = expression.getValue(context, String::class.java)
        */
        /**
         * StandardEvaluationContext()
         *
         * StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(this.beanFactory));
        Expression expression = parser.parseExpression("@someOtherBean.getData()");
        // or "@someOtherBean.data"
        final String value = expression.getValue(context, String.class);
         *
         */
        return proceed
    }



}