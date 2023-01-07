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
 * 「作成済記事の概要」の値オブジェクト
 *
 */
interface Description {
    /**
     * 概要
     */
    val value: String

    /**
     * Validation 有り、作成済記事の概要
     *
     * @property value
     */
    private data class ValidatedDescription(override val value: String) : Description

    /**
     * Validation 無し、作成済記事の概要
     *
     * @property value
     */
    private data class DescriptionWithoutValidation(override val value: String) : Description

    /**
     * Factory メソッド
     *
     * @constructor Create empty Companion
     */
    companion object {
        /**
         * Validation 有り
         *
         * @param description
         * @return
         */
        fun new(description: String?): ValidatedNel<ValidationError, Description> {
            val nonNullDescription = when (val result = ValidationError.Required.check(description)) {
                is Invalid -> return result.value.invalidNel()
                is Valid -> result
            }
            return ValidationError.TooLong.check(nonNullDescription.value)
                .map { ValidatedDescription(nonNullDescription.value) }
        }

        /**
         * Validation 無し
         *
         * @param description
         * @return
         */
        fun newWithoutValidation(description: String): Description = DescriptionWithoutValidation(description)
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
                get() = "description を入力してください"

            /**
             * Null 確認
             *
             * @param description
             * @return
             */
            fun check(description: String?): Validated<Required, String> =
                Option.fromNullable(description).fold(
                    { Invalid(Required) },
                    { Valid(it) }
                )
        }

        /**
         * 文字数制限
         *
         * 長すぎては駄目
         *
         * @property description
         */
        data class TooLong(val description: String) : ValidationError {
            companion object {
                private const val maximum: Int = 64

                /**
                 * 文字数確認
                 *
                 * @param description
                 * @return
                 */
                fun check(description: String): ValidatedNel<TooLong, Unit> =
                    if (description.length <= maximum) {
                        Unit.valid()
                    } else {
                        TooLong(description).invalidNel()
                    }
            }

            override val message: String
                get() = "description は $maximum 文字以下にしてください"
        }
    }
}
