package com.example.implementingserversidekotlindevelopment.util

/**
 * エラー型の戻り値
 *
 * 全ての処理の戻り値は、MyError インタフェースを実装したものに限る
 *
 */
interface MyError {
    /**
     * ドメインオブジェクトのバリデーションにおけるエラー型
     *
     * 必ずエラーメッセージを記述する
     *
     */
    interface ValidationError : MyError {
        /**
         * エラーメッセージ
         */
        val message: String
    }
}
