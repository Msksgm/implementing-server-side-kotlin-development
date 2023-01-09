package com.example.implementingserversidekotlindevelopment.api.integration

import com.example.implementingserversidekotlindevelopment.infra.DbConnection
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.api.DBRider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

class ArticleTest {
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DBRider
    class GetArticles(
        @Autowired val mockMvc: MockMvc,
    ) {
        @BeforeEach
        fun reset() = DbConnection.resetSequence()

        @Test
        @DataSet(
            value = [
                "datasets/yml/given/articles.yml"
            ]
        )
        fun `正常系-DB 内の全ての記事を取得する`() {
            /**
             * given:
             */

            /**
             * when:
             */
            val response = mockMvc.get("/api/articles") {
                contentType = MediaType.APPLICATION_JSON
            }.andReturn().response
            val actualStatus = response.status
            val actualResponseBody = response.contentAsString

            /**
             * then:
             * - ステータスコードが一致する
             * - レスポンスボディが一致する
             */
            val expectedStatus = 200
            val expectedResponseBody = """
                {
                  articleCount: 2,
                  articles: [
                    {
                      slug: "dummy-slug-01",
                      title: "dummy-title-01",
                      description: "dummy-description-01",
                      body:"dummy-body-01"
                    },
                    {
                      slug: "dummy-slug-02",
                      title: "dummy-title-02",
                      description: "dummy-description-02",
                      body: "dummy-body-02"
                    }
                  ]
                }
            """.trimIndent()
            assertThat(actualStatus).isEqualTo(expectedStatus)
            JSONAssert.assertEquals(
                expectedResponseBody,
                actualResponseBody,
                JSONCompareMode.NON_EXTENSIBLE
            )
        }
    }
}
