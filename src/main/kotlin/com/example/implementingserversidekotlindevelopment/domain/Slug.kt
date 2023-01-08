package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Option
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.valid
import com.example.implementingserversidekotlindevelopment.util.MyError
import java.util.UUID

/**
 * 作成済記事の Slug
 *
 */
interface Slug {
    /**
     * slug の値
     */
    val value: String

    /**
     * new から生成された slug
     *
     * @property value
     */
    private data class ValidatedSlug(override val value: String) : Slug

    /**
     * new 以外から生成された slug
     *
     * @property value
     */
    private data class SlugWithoutValidation(override val value: String) : Slug

    companion object {
        /**
         * Validation 無し
         *
         * @param slug
         * @return
         */
        fun newWithoutValidation(slug: String): Slug = SlugWithoutValidation(slug)

        /**
         * Validation 有り
         *
         * @param slug
         * @return
         */
        fun new(slug: String?): ValidatedNel<ValidationError, Slug> {
            val nonNullSlug = when (val result = ValidationError.Required.check(slug)) {
                is Validated.Invalid -> return result.value.invalidNel()
                is Validated.Valid -> result
            }
            return ValidationError.ValidFormat.check(nonNullSlug.value).map { ValidatedSlug(nonNullSlug.value) }
        }

        /**
         * 引数有りの場合、UUID から生成
         *
         * @return
         */
        fun new(): Slug {
            return ValidatedSlug(UUID.randomUUID().toString().split("-").joinToString(""))
        }
    }

    /**
     * Slug 生成時のドメインルール
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
                get() = "slug は必須です"

            /**
             * Null 確認
             *
             * @param slug
             * @return
             */
            fun check(slug: String?): Validated<Required, String> =
                Option.fromNullable(slug).fold({ Validated.Invalid(Required) }, { Validated.Valid(it) })
        }

        /**
         * フォーマット確認
         *
         * 指定された format 以外は駄目
         *
         * @property slug
         */
        data class ValidFormat(val slug: String) : ValidationError {
            companion object {
                private const val format: String = "^[a-z0-9]{32}$"

                /**
                 * フォーマット確認
                 *
                 * @param slug
                 * @return
                 */
                fun check(slug: String): ValidatedNel<ValidFormat, Unit> =
                    if (slug.matches(Regex(format))) {
                        Unit.valid()
                    } else {
                        ValidFormat(slug).invalidNel()
                    }
            }

            override val message: String
                get() = "slug は 32 文字の英小文字数字です。"
        }
    }
}
