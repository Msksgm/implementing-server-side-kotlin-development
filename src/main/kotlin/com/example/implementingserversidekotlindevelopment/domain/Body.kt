package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Option
import arrow.core.Validated
import arrow.core.Validated.Invalid
import arrow.core.Validated.Valid
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.valid
import com.example.implementingserversidekotlindevelopment.util.MyError

/**
 * 作成済記事の本文の値オブジェクト
 *
 */
interface Body {
    /**
     * 本文
     */
    val value: String

    /**
     * Validation 有り、作成済記事の本文
     *
     * @property value
     */
    private data class ValidatedBody(override val value: String) : Body

    /**
     * Validation 無し、作成済記事の本文
     *
     * @property value
     */
    private data class BodyWithoutValidation(override val value: String) : Body

    /**
     * Factory メソッド
     *
     * @constructor Create empty Companion
     */
    companion object {
        /**
         * Validation 無し
         *
         * @param body
         * @return
         */
        fun newWithoutValidation(body: String): Body = BodyWithoutValidation(body)

        /**
         * Validation 有り
         *
         * @param body
         * @return
         */
        fun new(body: String?): ValidatedNel<ValidationError, Body> {
            val nonNullBody = when (val result = ValidationError.Required.check(body)) {
                is Invalid -> return result.value.invalidNel()
                is Valid -> result
            }
            return ValidationError.TooLong.check(nonNullBody.value).map { ValidatedBody(nonNullBody.value) }
        }
    }

    /**
     * オブジェクト生成時のドメインルール
     *
     */
    sealed interface ValidationError : MyError.ValidationError {
        /**
         * 必須
         *
         * Null は許容しない
         *
         * @constructor Create empty Required
         */
        object Required : ValidationError {
            override val message: String
                get() = "body は必須です"

            /**
             * Null 確認
             *
             * @param body
             * @return
             */
            fun check(body: String?): Validated<Required, String> =
                Option.fromNullable(body).fold(
                    { Invalid(Required) },
                    { Valid(it) }
                )
        }

        /**
         * 文字数制限
         *
         * 長すぎては駄目
         *
         * @property body
         */
        data class TooLong(val body: String) : ValidationError {
            companion object {
                private const val maximum: Int = 1024

                /**
                 * 文字数確認
                 *
                 * @param body
                 * @return
                 */
                fun check(body: String): ValidatedNel<TooLong, Unit> =
                    if (body.length <= maximum) {
                        Unit.valid()
                    } else {
                        TooLong(body).invalidNel()
                    }
            }

            override val message: String
                get() = "body は $maximum 文字以下にしてください"
        }
    }
}
