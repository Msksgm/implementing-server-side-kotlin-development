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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class ShowArticleUseCaseTest {
    @Nested
    class Normal {
        data class NormalTestCase(
            val title: String,
            val articleRepositoryFindBySlugResult: Either<ArticleRepository.FindBySlugError, CreatedArticle>,
            val expected: Either<ShowArticleUseCase.Error, CreatedArticle>,
        )

        @TestFactory
        fun executeNormalTest(): Stream<DynamicNode> {
            return Stream.of(
                NormalTestCase(
                    "正常系: ArticleRepository.findBySlug が CreatedArticle を返す場合、CreatedArticle が戻り値",
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
                     * - 有効な slug（32 文字の英数字）
                     */
                    val slug = "01234567890123456789012345678901"
                    val useCase = ShowArticleUseCaseImpl(
                        object : ArticleRepositoryImpl() {
                            override fun findBySlug(slug: Slug): Either<ArticleRepository.FindBySlugError, CreatedArticle> {
                                return testCase.articleRepositoryFindBySlugResult
                            }
                        }
                    )

                    /**
                     * when:
                     */
                    val actual = useCase.execute(slug)

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }
    }
}
