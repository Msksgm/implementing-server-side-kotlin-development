package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.handleError
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.util.ValidationError
import org.springframework.stereotype.Service

/**
 * 記事削除ユースケース
 *
 */
interface DeleteCreatedArticleUseCase {
    /**
     * 記事削除
     *
     * @param slug
     * @return
     */
    fun execute(slug: String?): Either<Error, Unit> = throw NotImplementedError()

    /**
     * 記事削除ユースケースのエラー
     *
     */
    sealed interface Error {
        /**
         * バリデーションエラー
         *
         * @property errors
         */
        data class ValidationErrors(val errors: List<ValidationError>) : Error

        /**
         * Slug に該当する記事が見つからなかった
         *
         * @property slug
         */
        data class NotFoundArticleBySlug(val slug: Slug) : Error
    }
}

/**
 * 記事削除ユースケースの具象クラス
 *
 * @property articleRepository
 */
@Service
class DeleteCreatedArticleUseCaseImpl(
    val articleRepository: ArticleRepository,
) : DeleteCreatedArticleUseCase {
    override fun execute(slug: String?): Either<DeleteCreatedArticleUseCase.Error, Unit> {
        val validatedSlug = Slug.new(slug = slug).fold(
            { TODO() },
            { it }
        )

        articleRepository.delete(validatedSlug)
            .handleError {
                when (it) {
                    is ArticleRepository.DeleteError.NotFound -> TODO()
                }
            }

        return Unit.right()
    }
}
