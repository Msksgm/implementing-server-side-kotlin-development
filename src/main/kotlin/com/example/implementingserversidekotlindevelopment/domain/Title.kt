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
 * 作成済記事のタイトルの値オブジェクト
 *
 */
interface Title {
    /**
     * タイトル名
     */
    val value: String

    /**
     * Validation 有り、作成済記事のタイトル
     *
     * @property value
     */
    private data class ValidatedTitle(override val value: String) : Title

    /**
     * Validation 無し、作成済記事のタイトル
     *
     * @property value
     */
    private data class TitleWithoutValidation(override val value: String) : Title

    /**
     * Factory メソッド
     *
     * @constructor Create empty Companion
     */
    companion object {
        /**
         * Validation 無し
         *
         * @param title
         * @return
         */
        fun newWithoutValidation(title: String): Title = TitleWithoutValidation(title)

        /**
         * Validation 有り
         *
         * @param title
         * @return
         */
        fun new(title: String?): ValidatedNel<ValidationError, Title> {
            val nonNullTitle = when (val result = ValidationError.Required.check(title)) {
                is Invalid -> return result.value.invalidNel()
                is Valid -> result
            }
            return ValidationError.TooLong.check(nonNullTitle.value).map { ValidatedTitle(nonNullTitle.value) }
        }
    }

    /**
     * タイトル生成時のドメインルール
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
                get() = "title は必須です"

            /**
             * Null 確認
             *
             * @param title
             * @return
             */
            fun check(title: String?): Validated<Required, String> =
                Option.fromNullable(title).fold(
                    { Invalid(Required) },
                    { Valid(it) }
                )
        }

        /**
         * 文字数制限
         *
         * 長すぎては駄目
         *
         * @property title
         */
        data class TooLong(val title: String) : ValidationError {
            companion object {
                private const val maximum: Int = 32

                /**
                 * 文字数確認
                 *
                 * @param title
                 * @return
                 */
                fun check(title: String): ValidatedNel<TooLong, Unit> =
                    if (title.length <= maximum) {
                        Unit.valid()
                    } else {
                        TooLong(title).invalidNel()
                    }
            }

            override val message: String
                get() = "title は $maximum 文字以下にしてください。"
        }
    }
}
