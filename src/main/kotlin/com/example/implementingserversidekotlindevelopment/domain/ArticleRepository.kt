package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Either

/**
 * 作成済記事のリポジトリ
 *
 */
interface ArticleRepository {
    /**
     * 作成済記事の一覧取得
     *
     * @return
     */
    fun find(): Either<FindError, List<CreatedArticle>> = throw NotImplementedError()

    /**
     * ArticleRepository.find のエラーインタフェース
     *
     * エラーになるパターンが存在しない
     */
    sealed interface FindError
}
