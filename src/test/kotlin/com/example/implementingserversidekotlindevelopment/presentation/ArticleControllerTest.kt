package com.example.implementingserversidekotlindevelopment.presentation

import arrow.core.Either
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.Article
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.MultipleArticleResponse
import com.example.implementingserversidekotlindevelopment.usecase.FeedArticleUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.stream.Stream

class ArticleControllerTest {
    @Nested
    class GetArticle {
        private fun articleController(
            feedArticleUseCase: FeedArticleUseCase,
        ): ArticleController = ArticleController(feedArticleUseCase)

        data class NormalTestCase(
            val title: String,
            val useCaseExecuteResult: Either<FeedArticleUseCase.FeedArticleUseCaseError, FeedArticleUseCase.FeedCreatedArticles>,
            val expected: ResponseEntity<MultipleArticleResponse>,
        )

        @TestFactory
        fun getArticleNormalTest(): Stream<DynamicNode> {
            return Stream.of(
                NormalTestCase(
                    "正常系: UseCase が FeedArticleUseCase.ShowCreatedArticles を返す場合、200 レスポンスを返す",
                    FeedArticleUseCase.FeedCreatedArticles(
                        articlesCount = 2,
                        articles = listOf(
                            CreatedArticle.newWithoutValidation(
                                slug = Slug.newWithoutValidation("dummy-slug-01"),
                                title = Title.newWithoutValidation("dummy-title-01"),
                                description = Description.newWithoutValidation("dummy-description-01"),
                                body = Body.newWithoutValidation("dummy-body-01")
                            ),
                            CreatedArticle.newWithoutValidation(
                                slug = Slug.newWithoutValidation("dummy-slug-02"),
                                title = Title.newWithoutValidation("dummy-title-02"),
                                description = Description.newWithoutValidation("dummy-description-02"),
                                body = Body.newWithoutValidation("dummy-body-02")
                            ),
                        )
                    ).right(),
                    ResponseEntity<MultipleArticleResponse>(
                        MultipleArticleResponse(
                            articleCount = 2,
                            articles = listOf(
                                Article(
                                    slug = "dummy-slug-01",
                                    title = "dummy-title-01",
                                    description = "dummy-description-01",
                                    body = "dummy-body-01"
                                ),
                                Article(
                                    slug = "dummy-slug-02",
                                    title = "dummy-title-02",
                                    description = "dummy-description-02",
                                    body = "dummy-body-02"
                                ),
                            )
                        ),
                        HttpStatus.OK
                    )
                )
            ).map { testCase ->
                dynamicTest(testCase.title) {
                    /**
                     * given:
                     */
                    val controller = articleController(object : FeedArticleUseCase {
                        override fun execute(): Either<FeedArticleUseCase.FeedArticleUseCaseError, FeedArticleUseCase.FeedCreatedArticles> {
                            return testCase.useCaseExecuteResult
                        }
                    })

                    /**
                     * when:
                     */
                    val actual = controller.getArticles()

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }
    }
}
