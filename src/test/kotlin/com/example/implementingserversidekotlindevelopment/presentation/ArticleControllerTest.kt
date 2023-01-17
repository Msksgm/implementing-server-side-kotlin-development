package com.example.implementingserversidekotlindevelopment.presentation

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.Article
import com.example.implementingserversidekotlindevelopment.openapi.generated.model.SingleArticleResponse
import com.example.implementingserversidekotlindevelopment.usecase.ShowArticleUseCase
import com.example.implementingserversidekotlindevelopment.util.ValidationError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.stream.Stream

class ArticleControllerTest {
    @Nested
    class GetArticle {
        data class NormalTestCase(
            val title: String,
            val useCaseExecuteResult: Either<ShowArticleUseCase.Error, CreatedArticle>,
            val expected: ResponseEntity<SingleArticleResponse>,
        )

        @TestFactory
        fun getArticleNormalTest(): Stream<DynamicNode> {
            return Stream.of(
                NormalTestCase(
                    "正常系: UseCase が CreatedArticle を返す場合、200 レスポンスを戻す",
                    CreatedArticle.newWithoutValidation(
                        slug = Slug.newWithoutValidation("dummy-slug"),
                        title = Title.newWithoutValidation("dummy-title"),
                        description = Description.newWithoutValidation("dummy-description"),
                        body = Body.newWithoutValidation("dummy-body")
                    ).right(),
                    ResponseEntity<SingleArticleResponse>(
                        SingleArticleResponse(
                            Article(
                                slug = "dummy-slug",
                                title = "dummy-title",
                                description = "dummy-description",
                                body = "dummy-body"
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
                    val controller = ArticleController(
                        object : ShowArticleUseCase {
                            override fun execute(slug: String?): Either<ShowArticleUseCase.Error, CreatedArticle> {
                                return testCase.useCaseExecuteResult
                            }
                        })

                    /**
                     * when:
                     */
                    val actual = controller.getArticle("dummy-slug")

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }

        data class AbnormalTestCase(
            val title: String,
            val useCaseExecuteResult: Either<ShowArticleUseCase.Error, CreatedArticle>,
            val expected: ArticleController.ShowArticleUseCaseErrorException,
        )

        private val notFoundArticleBySlug =
            ShowArticleUseCase.Error.NotFoundArticleBySlug(Slug.newWithoutValidation("dummy-slug"))

        private val validationErrors =
            ShowArticleUseCase.Error.ValidationErrors(
                errors = listOf(object : ValidationError {
                    override val message: String
                        get() = "slug が不正です"
                })
            )

        @TestFactory
        fun getArticleAbnormalTest(): Stream<DynamicNode> {
            return Stream.of(
                AbnormalTestCase(
                    "準正常系: UseCase が NotFoundArticleBySlug を返す場合、ShowArticleUseCaseErrorException が発生する",
                    notFoundArticleBySlug.left(),
                    ArticleController.ShowArticleUseCaseErrorException(
                        notFoundArticleBySlug
                    ),
                ),
                AbnormalTestCase(
                    "準正常系: UseCase が ValidationErrors を返す場合、ShowArticleUseCaseErrorException が発生する",
                    validationErrors.left(),
                    ArticleController.ShowArticleUseCaseErrorException(validationErrors),
                )
            ).map { testCase ->
                dynamicTest(testCase.title) {
                    /**
                     * given:
                     */
                    val controller = ArticleController(
                        object : ShowArticleUseCase {
                            override fun execute(slug: String?): Either<ShowArticleUseCase.Error, CreatedArticle> {
                                return testCase.useCaseExecuteResult
                            }
                        })

                    /**
                     * when:
                     */
                    val actual =
                        assertThrows<ArticleController.ShowArticleUseCaseErrorException> { controller.getArticle("dummy-slug") }

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }
    }
}
