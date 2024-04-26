package com.dn0ne.moneymate.app.domain.util

import com.dn0ne.moneymate.app.domain.extensions.toInstant
import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class RealmInstantSerializer : KSerializer<RealmInstant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RealmInstant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): RealmInstant {
        val instant = Instant.parse(decoder.decodeString())

        return RealmInstant.from(
            epochSeconds = instant.epochSeconds,
            nanosecondAdjustment = instant.nanosecondsOfSecond
        )
    }

    override fun serialize(encoder: Encoder, value: RealmInstant) {
        encoder.encodeString(value.toInstant().toString())
    }
}