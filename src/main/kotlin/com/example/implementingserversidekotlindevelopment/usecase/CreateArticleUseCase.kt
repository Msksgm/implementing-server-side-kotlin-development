package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.util.ValidationError
import org.springframework.stereotype.Service
import java.lang.UnsupportedOperationException

/**
 * 記事作成ユースケース
 *
 */
interface CreateArticleUseCase {
    /**
     * 記事作成
     *
     * @param title
     * @param description
     * @param body
     * @return
     */
    fun execute(title: String?, description: String?, body: String?): Either<Error, CreatedArticle> =
        throw NotImplementedError()

    /**
     * 記事作成ユースケースのエラー
     *
     */
    sealed interface Error {
        /**
         * 記事のリクエストが不正だった
         *
         * @property errors
         */
        data class InvalidArticle(val errors: List<ValidationError>) : Error
    }
}

/**
 * 記事作成ユースケース取得クラス
 *
 * @property articleRepository
 */
@Service
class CreateArticleUseCaseImpl(
    val articleRepository: ArticleRepository,
) : CreateArticleUseCase {
    override fun execute(
        title: String?,
        description: String?,
        body: String?,
    ): Either<CreateArticleUseCase.Error, CreatedArticle> {
        val unsavedCreatedArticle = CreatedArticle.new(title, description, body).fold(
            { return CreateArticleUseCase.Error.InvalidArticle(it).left() },
            { it }
        )

        val createdArticle = articleRepository.create(createdArticle = unsavedCreatedArticle).fold(
            { throw UnsupportedOperationException("現在この分岐に入ることはない") },
            { it }
        )

        return createdArticle.right()
    }
}
