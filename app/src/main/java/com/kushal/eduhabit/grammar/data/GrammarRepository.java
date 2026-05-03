package com.kushal.eduhabit.grammar.data;

import com.kushal.eduhabit.grammar.model.QuizQuestion;
import java.util.ArrayList;
import java.util.List;

public class GrammarRepository {

    public static List<QuizQuestion> getQuestionsForLevel(String level) {
        List<QuizQuestion> questions = new ArrayList<>();
        
        switch (level) {
            case "Beginner":
                questions.add(new QuizQuestion("She ___ to school every day.", List.of("go", "goes", "gone", "going"), 1, "Present simple 'goes' is used for third-person singular habits."));
                questions.add(new QuizQuestion("I have ___ apple in my bag.", List.of("a", "an", "the", "no"), 1, "Use 'an' before words starting with a vowel sound."));
                questions.add(new QuizQuestion("They ___ playing football now.", List.of("is", "am", "are", "be"), 2, "Present continuous requires 'are' for plural subjects."));
                questions.add(new QuizQuestion("He is taller ___ his brother.", List.of("then", "than", "that", "to"), 1, "Use 'than' for comparisons."));
                questions.add(new QuizQuestion("We went ___ the park yesterday.", List.of("to", "at", "on", "in"), 0, "Use 'to' to indicate a destination."));
                break;
                
            case "Intermediate":
                questions.add(new QuizQuestion("If it ___ tomorrow, we will cancel the trip.", List.of("rains", "will rain", "rained", "rain"), 0, "In first conditional, use present simple after 'if'."));
                questions.add(new QuizQuestion("I ___ in Nepal for five years.", List.of("am living", "have lived", "lived", "live"), 1, "Present perfect is used for actions starting in the past and continuing now."));
                questions.add(new QuizQuestion("He suggested that she ___ a doctor.", List.of("sees", "see", "saw", "should see"), 1, "The subjunctive mood uses the base form 'see' after suggest."));
                questions.add(new QuizQuestion("By the time you arrive, I ___ my work.", List.of("will finish", "finish", "will have finished", "finished"), 2, "Future perfect describes actions completed before a future time."));
                break;
                
            case "Advanced":
                questions.add(new QuizQuestion("Hardly ___ the station when the train left.", List.of("I reached", "had I reached", "I had reached", "did I reach"), 1, "Inverted word order is used after 'Hardly' at the start of a sentence."));
                questions.add(new QuizQuestion("It's high time you ___ studying.", List.of("start", "started", "starting", "should start"), 1, "After 'It's high time', use the past simple to imply urgency."));
                questions.add(new QuizQuestion("___ of the two candidates was suitable.", List.of("Neither", "None", "No one", "Any"), 0, "Use 'Neither' when referring to two people or things."));
                break;
        }
        return questions;
    }
}
