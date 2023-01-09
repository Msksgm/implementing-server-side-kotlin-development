package com.example.implementingserversidekotlindevelopment.presentation

import com.example.implementingserversidekotlindevelopment.openapi.generated.controller.ArticlesApi
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.Article
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.MultipleArticleResponse
import com.example.implementingserversidekotlindevelopment.usecase.FeedArticleUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * 作成済記事のコントローラー
 *
 * @property feedArticleUseCase
 */
@RestController
class ArticleController(val feedArticleUseCase: FeedArticleUseCase) : ArticlesApi {
    override fun getArticles(): ResponseEntity<MultipleArticleResponse> {
        val useCaseResult = feedArticleUseCase.execute().fold(
            { throw UnsupportedOperationException("想定外のエラー") },
            { it }
        )
        return ResponseEntity(
            MultipleArticleResponse(
                articleCount = useCaseResult.articlesCount,
                articles = useCaseResult.articles.map {
                    Article(
                        slug = it.slug.value,
                        title = it.title.value,
                        description = it.description.value,
                        body = it.body.value
                    )
                }
            ),
            HttpStatus.OK
        )
    }
}
