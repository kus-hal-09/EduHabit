package com.kushal.eduhabit.grammar.data;

import com.kushal.eduhabit.grammar.model.GrammarLesson;
import java.util.ArrayList;
import java.util.List;

public class LessonRepository {
    public List<GrammarLesson> getGrammarLessons() {
        List<GrammarLesson> lessons = new ArrayList<>();
        lessons.add(new GrammarLesson("1", "Subjunctive Mood", "Expressing wishes and hypotheticals.", 
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4", 75));
        lessons.add(new GrammarLesson("2", "Relative Clauses", "Connecting ideas with who, which, that.", 
            "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_Concert_720p.mp4", 30));
        lessons.add(new GrammarLesson("3", "Inversion", "Emphasizing sentences by switching word order.", 
            "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", 10));
        return lessons;
    }
}
