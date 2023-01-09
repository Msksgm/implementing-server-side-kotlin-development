package com.example.implementingserversidekotlindevelopment.infra

import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import org.springframework.stereotype.Repository

/**
 * 作成済記事のリポジトリの具象クラス
 *
 */
@Repository
class ArticleRepositoryImpl : ArticleRepository