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

class FeedArticleUseCaseTest {
    @Nested
    class Normal {
        private fun feedArticleUseCase(
            articleRepository: ArticleRepository,
        ): FeedArticleUseCase = FeedArticleUseCaseImpl(articleRepository)

        data class NormalTestCase(
            val title: String,
            val articleRepositoryFindResult: Either<ArticleRepository.FindError, List<CreatedArticle>>,
            val expected: Either<FeedArticleUseCase.Error, FeedArticleUseCase.FeedCreatedArticles>,
        )

        @TestFactory
        fun executeNormalTest(): Stream<DynamicNode> {
            return Stream.of(
                NormalTestCase(
                    "正常系: ArticleRepository.find が List<CreatedArticle> を返す場合、FeedArticleUseCase.FeedCreatedArticles が戻り値",
                    listOf(
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
                    ).right(),
                    FeedArticleUseCase.FeedCreatedArticles(
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
                        ),
                        articlesCount = 2
                    ).right()
                )
            ).map { testCase ->
                dynamicTest(testCase.title) {
                    /**
                     * given:
                     */
                    val useCase = feedArticleUseCase(
                        object : ArticleRepositoryImpl() {
                            override fun find(): Either<ArticleRepository.FindError, List<CreatedArticle>> {
                                return testCase.articleRepositoryFindResult
                            }
                        }
                    )

                    /**
                     * when:
                     */
                    val actual = useCase.execute()

                    /**
                     * then:
                     */
                    assertThat(actual).isEqualTo(testCase.expected)
                }
            }
        }
    }
}
