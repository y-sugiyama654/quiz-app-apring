package com.example.quiz.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quiz.entity.Quiz;
import com.example.quiz.form.QuizForm;
import com.example.quiz.service.QuizService;

/**
 * Quizコントローラ
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {
	
	/**
	 * DI対象
	 */
	@Autowired
	QuizService service;
	
	/**
	 * 「form-backing bean」の初期化
	 */
	@ModelAttribute
	public QuizForm setUpForm() {
		QuizForm form = new QuizForm();
		
		form.setAnswer(true);
		
		return form;
	}
	
	/**
	 * Quizの一覧を表示
	 */
	@GetMapping
	public String showList(QuizForm quizForm, Model model) {		
		quizForm.setNewQuiz(true);
		
		// クイズの一覧を取得する
		Iterable<Quiz> list = service.selectAll();
		
		// 表示用Modelへ格納
		model.addAttribute("list", list);
		model.addAttribute("title", "登録用フォーム");
		
		return "crud";
	}
	
	/**
	 * Quizデータを1件登録
	 */
	@PostMapping("/insert")
	public String insert(@Validated QuizForm quizForm, BindingResult bindingResult, 
			Model model, RedirectAttributes redirectAttributes) {	
		// FormからEntityに詰め替え
		Quiz quiz = new Quiz();
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		
		// 入力チェック
		if (!bindingResult.hasErrors()) {
			service.insertQuiz(quiz);
			redirectAttributes.addAttribute("complete", "登録が完了しました。");
			return "redirect:/quiz";
		} else {
			return showList(quizForm, model);
		}

	}
	
	/*
	 * Quizデータを1件取得しフォームに表示
	 */
	@GetMapping("/{id}")
	public String showUpdate(QuizForm quizForm, @PathVariable Integer id, Model model) {
		// Quizを取得
		Optional<Quiz> quizOpt = service.selectOneById(id);
		
		// QuizFormへ詰め直し
		Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
		
		// QuizFormがnullでなければ中身を取り出す
		if (quizFormOpt.isPresent()) {
			quizForm = quizFormOpt.get();
		}
		
		// 更新用のModelを作成
		makeUpdateModel(quizForm, model);
		
		return "crud";
	}
	
	/**
	 * idをkeyにしてデータを更新
	 */
	@PostMapping("/update")
	public String update(
			@Validated QuizForm quizForm, 
			BindingResult bindingResult, 
			Model model, 
			RedirectAttributes redirectAttributes) {
		
		// QuizFormからQuizに詰め直す
		Quiz quiz = makeQuiz(quizForm);
		
		// 入力チェック
		if (!bindingResult.hasErrors()) {
			service.updateQuiz(quiz);
			redirectAttributes.addFlashAttribute("complete", "更新が完了しました。");
			
			return "redirect:/quiz/" + quiz.getId();
		} else {
			makeUpdateModel(quizForm, model);
			
			return "crud";
		}
	}
	
	/**
	 * 更新用のModelを作成
	 */
	private void makeUpdateModel(QuizForm quizForm, Model model) {
		model.addAttribute("id", quizForm.getId());
		quizForm.setNewQuiz(false);
		model.addAttribute("quizForm", quizForm);
		model.addAttribute("title", "更新用フォーム");
	}
	
	/**
	 * QuizFormからQuizへ詰め直す
	 */
	private Quiz makeQuiz(QuizForm quizForm) {
		Quiz quiz = new Quiz();
		quiz.setId(quizForm.getId());
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		
		return quiz;
	}
	
	/**
	 * QuizからQuizFormへ詰め直す
	 */
	private QuizForm makeQuizForm(Quiz quiz) {
		QuizForm form = new QuizForm();
		form.setId(quiz.getId());
		form.setQuestion(quiz.getQuestion());
		form.setAnswer(quiz.getAnswer());
		form.setAuthor(quiz.getAuthor());
		form.setNewQuiz(false);
		
		return form;
	}
	
	/**
	 * idをkeyにしてデータを削除
	 */
	@PostMapping("/delete")
	public String delete(
			@RequestParam("id") String id,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		service.deleteQuizById(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました。");
		
		return "redirect:/quiz";
	}
	
	/*
	 * Quizデータをランダムで1件取得し表示
	 */
	@GetMapping("/play")
	public String showQuiz(QuizForm quizForm, Model model) {
		Optional<Quiz> quizOpt = service.selectOneRandom();
		
		if (quizOpt.isPresent()) {
			// QuizFormへ詰め直し
			Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
			quizForm = quizFormOpt.get();
		} else {
			model.addAttribute("msg", "問題がありません。");
			
			return "play";
		}
		
		model.addAttribute("quizForm", quizForm);
		
		return "play";
	}
	
	/**
	 * クイズの正解／不正解を判定
	 */
	@PostMapping("/check")
	public String checkQuiz(QuizForm quizForm, @RequestParam Boolean answer, Model model) {
		if (service.checkQuiz(quizForm.getId(), answer)) {
			model.addAttribute("msg", "正解です！");
		} else {
			model.addAttribute("msg", "残念、不正解です。");
		}
		
		return "answer";
	}

}
