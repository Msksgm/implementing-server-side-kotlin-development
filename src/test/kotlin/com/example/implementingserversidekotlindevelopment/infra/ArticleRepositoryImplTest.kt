package com.example.implementingserversidekotlindevelopment.infra

import arrow.core.Either
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.api.DBRider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class ArticleRepositoryImplTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DBRider
    class Find {
        @BeforeAll
        fun reset() = DbConnection.resetSequence()

        @Test
        @DataSet(
            value = [
                "datasets/yml/given/empty-articles.yml"
            ]
        )
        fun `正常系-作成済記事が 1 つもない場合、からの作成済記事の一覧が戻り値`() {
            /**
             * given:
             */
            val articleRepository = ArticleRepositoryImpl(DbConnection.namedParameterJdbcTemplate)

            /**
             * when:
             */
            val actual = articleRepository.find()

            /**
             * then:
             */
            val expected = listOf<CreatedArticle>().right()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DataSet(
            value = [
                "datasets/yml/given/articles.yml"
            ]
        )
        fun `正常系-DB に作成済記事がある場合、全ての作成済記事の一覧が戻り値`() {
            /**
             * given:
             */
            val articleRepository = ArticleRepositoryImpl(DbConnection.namedParameterJdbcTemplate)

            /**
             * when:
             */
            val actual = articleRepository.find()

            /**
             * then:
             */
            val expected = listOf(
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
            when (actual) {
                is Either.Left -> assert(false)
                is Either.Right -> {
                    val createdArticleList = actual.value
                    assertThat(createdArticleList).hasSameElementsAs(expected)
                }
            }
        }
    }
}