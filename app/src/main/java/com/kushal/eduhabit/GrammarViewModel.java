package com.kushal.eduhabit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class GrammarViewModel extends ViewModel {
    private MutableLiveData<List<GrammarModule>> modules;

    public MutableLiveData<List<GrammarModule>> getModules() {
        if (modules == null) {
            modules = new MutableLiveData<>();
            loadModules();
        }
        return modules;
    }

    private void loadModules() {
        List<GrammarModule> list = new ArrayList<>();

        // 1. Subjunctive Mood
        List<GrammarModule.QuizQuestion> q1 = new ArrayList<>();
        q1.add(new GrammarModule.QuizQuestion("Which sentence uses the subjunctive mood correctly?",
                List.of("I suggest that he goes home.", "I suggest that he go home.", "I suggest that he gone home.", "I suggest that he went home."), 1));
        list.add(new GrammarModule("Subjunctive Mood", "Learn how to express wishes, suggestions, and hypothetical situations.", "6H-f_LY_688", 
                "The subjunctive mood is used to express various states of unreality such as wish, emotion, possibility, judgment, opinion, necessity, or action that has not yet occurred.",
                List.of("Used after verbs like 'suggest', 'recommend', 'insist'.", "The form is usually the base form of the verb (e.g., 'be', 'go').", "Common in formal English and legal writing."), q1));

        // 2. Relative Clauses
        List<GrammarModule.QuizQuestion> q2 = new ArrayList<>();
        q2.add(new GrammarModule.QuizQuestion("Which relative pronoun is used for non-essential information in a non-defining relative clause?",
                List.of("That", "Who", "Which", "Whom"), 2));
        list.add(new GrammarModule("Relative Clauses", "Master the use of defining and non-defining relative clauses.", "o_v9W4X_X_Y", 
                "Relative clauses give us more information about a person or thing. We use relative pronouns to introduce relative clauses.",
                List.of("Defining clauses are essential to the meaning of the sentence.", "Non-defining clauses provide extra, non-essential information.", "Use 'who' for people and 'which' or 'that' for things."), q2));

        // 3. Inversion
        List<GrammarModule.QuizQuestion> q3 = new ArrayList<>();
        q3.add(new GrammarModule.QuizQuestion("Complete the sentence: 'Never ___ such a beautiful sunset.'",
                List.of("I have seen", "I saw", "have I seen", "had I see"), 2));
        list.add(new GrammarModule("Inversion", "Understand how to invert subject and verb for emphasis.", "trMVny_6X_Y", 
                "Inversion happens when we reverse the normal order of the subject and the verb. It's often used after negative adverbials.",
                List.of("Common after 'Never', 'Rarely', 'Seldom'.", "Creates a more formal or dramatic effect.", "Follows the pattern: Adverbial + Auxiliary Verb + Subject + Main Verb."), q3));

        // 4. Cleft Sentences
        List<GrammarModule.QuizQuestion> q4 = new ArrayList<>();
        q4.add(new GrammarModule.QuizQuestion("What is the purpose of a cleft sentence?",
                List.of("To shorten a sentence", "To emphasize a specific part of the sentence", "To make a sentence more complex", "To avoid using relative pronouns"), 1));
        list.add(new GrammarModule("Cleft Sentences", "Learn how to use 'It' and 'What' clefts to focus information.", "v_X4W4_X_Y", 
                "A cleft sentence is a sentence in which some constituent is moved from its regular position into a separate clause to give it greater emphasis.",
                List.of("Starts with 'It is/was' or 'What...'.", "Helps to focus on specific information.", "Useful in both written and spoken English for clarity."), q4));

        modules.setValue(list);
    }
}
