package com.example.implementingserversidekotlindevelopment.infra

import arrow.core.Either
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * 作成済記事のリポジトリの具象クラス
 *
 * @property namedParameterJdbcTemplate
 */
@Repository
class ArticleRepositoryImpl(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : ArticleRepository {
    override fun find(): Either<ArticleRepository.FindError, List<CreatedArticle>> {
        val sql = """
            SELECT
                articles.slug
                , articles.title
                , articles.body
                , articles.description
            FROM
                articles
            ;
        """.trimIndent()
        val articleMap = namedParameterJdbcTemplate.queryForList(sql, MapSqlParameterSource())
        return articleMap.map {
            CreatedArticle.newWithoutValidation(
                Slug.newWithoutValidation(it["slug"].toString()),
                Title.newWithoutValidation(it["title"].toString()),
                Description.newWithoutValidation(it["description"].toString()),
                Body.newWithoutValidation(it["body"].toString())
            )
        }.right()
    }
}
