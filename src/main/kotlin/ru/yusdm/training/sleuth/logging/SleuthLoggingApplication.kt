package ru.yusdm.training.sleuth.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SleuthLoggingApplication

/**
 * BaggageField REQUEST_ID = BaggageField.create("x-vcap-request-id");
BaggageField USER_ID = BaggageField.create("userId");

Tracing.newBuilder().propagationFactory(
BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
.add(SingleBaggageField.remote(REQUEST_ID))
.add(SingleBaggageField.newBuilder(USER_ID).addKeyName("baggage-user-id").build())
.build());
 */
fun main(args: Array<String>) {

//	Tracing.newBuilder().propagationFactory(
//		BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
//			.add(
//				SingleBaggageField.newBuilder(BaggageField.create("x-vcap-request-id")).build()
//			).build()
//	)

	//ExtraFieldPropagation.set("x-b3-foo", "value");

/*	Tracing.newBuilder().propagationFactory(
		ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "x-vcap-request-id")
	);*/
	runApplication<SleuthLoggingApplication>(*args)
}
/*
@Component
class SleuthCustomFactory: Propagation<String> {

	override fun keys(): MutableList<String> {
		return mutableListOf("key_1", "key_2")
	}

	override fun <R : Any?> injector(setter: Propagation.Setter<R, String>?): TraceContext.Injector<R> {
		TODO("Not yet implemented")
	}

	override fun <R : Any?> extractor(getter: Propagation.Getter<R, String>?): TraceContext.Extractor<R> {
		TODO("Not yet implemented")
	}

}*/
