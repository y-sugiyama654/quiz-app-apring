package com.example.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuizRepository;

@SpringBootApplication
public class QuizApplication {

	/**
	 * 起動メソッド
	 */
	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args)
		.getBean(QuizApplication.class).execute();
	}
	
	/**
	 * 注入
	 */
	@Autowired
	QuizRepository repository;
	private void execute() {
		// 登録処理
		setup();
		// 全件取得
		showList();
	}
	
	/**
	 * クイズを2件作成
	 */
	private void setup() {
		
		// データの全件削除処理
		repository.deleteAll();
		
		// エンティティの生成
		Quiz quiz1 = new Quiz(null, "「Spring」はフレームワークか？", true, "登録太郎");
		// 登録処理
		quiz1 = repository.save(quiz1);
		System.out.println("登録したデータは、" + quiz1 + "です。");
		
		// エンティティの生成
		Quiz quiz2 = new Quiz(null, "「Spring MVC」はバッチ機能を提供するか？", false, "登録太郎");
		// 登録処理
		quiz2 = repository.save(quiz2);
		System.out.println("登録したデータは、" + quiz2 + "です。");
	}
	
	/**
	 * 全件取得
	 */
	private void showList() {
		System.out.println("-----全件取得処理の開始-----");
		Iterable<Quiz> quizzes = repository.findAll();
		for (Quiz quiz: quizzes) {
			System.out.println(quiz);
		}
		System.out.println("-----全件取得処理の完了-----");
	}

}
