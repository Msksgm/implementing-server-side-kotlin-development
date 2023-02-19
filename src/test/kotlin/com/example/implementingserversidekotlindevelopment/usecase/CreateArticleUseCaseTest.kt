package com.example.implementingserversidekotlindevelopment.usecase

import arrow.core.Either
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
import com.example.implementingserversidekotlindevelopment.infra.ArticleRepositoryImpl
import com.example.implementingserversidekotlindevelopment.infra.DbConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class CreateArticleUseCaseTest {
    @Nested
    class Normal {
        data class NormalTestCase(
            val title: String,
            val articleRepositoryCreateResult: Either<ArticleRepository.CreateArticleError, CreatedArticle>,
            val expected: Either<CreateArticleUseCase.Error, CreatedArticle>,
        )

        @TestFactory
        fun executeNormalTest(): Stream<DynamicNode> {
            return Stream.of(
                NormalTestCase(
                    "正常系: ArticleRepository.create が CreatedArticle を返す場合、CreatedArticle が戻り値",
                    CreatedArticle.newWithoutValidation(
                        slug = Slug.newWithoutValidation("dummy-slug"),
                        title = Title.newWithoutValidation("dummy-title"),
                        description = Description.newWithoutValidation("dummy-description"),
                        body = Body.newWithoutValidation("dummy-body")
                    ).right(),
                    CreatedArticle.newWithoutValidation(
                        slug = Slug.newWithoutValidation("dummy-slug"),
                        title = Title.newWithoutValidation("dummy-title"),
                        description = Description.newWithoutValidation("dummy-description"),
                        body = Body.newWithoutValidation("dummy-body")
                    ).right(),
                )
            ).map { testCase ->
                dynamicTest(testCase.title) {
                    /**
                     * given:
                     * - 有効な title、description、body
                     */
                    val title = "dummy-title"
                    val description = "dummy-description"
                    val body = "dummy-body"
                    val useCase = CreateArticleUseCaseImpl(object :
                            ArticleRepositoryImpl(DbConnection.namedParameterJdbcTemplate) {
                            override fun create(createdArticle: CreatedArticle): Either<ArticleRepository.CreateArticleError, CreatedArticle> {
                                return testCase.articleRepositoryCreateResult
                            }
                        })

                    /**
                     * when:
                     */
                    val actual = useCase.execute(title, description, body)

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }
    }
}
