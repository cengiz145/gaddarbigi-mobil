package com.example.gaddarquiz.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.gaddarquiz.model.Question;
import com.example.gaddarquiz.model.QuestionCategory;
import com.example.gaddarquiz.model.QuestionDifficulty;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Question> __insertionAdapterOfQuestion;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfRecordCorrectAnswer;

  private final SharedSQLiteStatement __preparedStmtOfRecordWrongAnswer;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestion = new EntityInsertionAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`text`,`options`,`correctAnswerIndex`,`category`,`difficulty`,`askedCount`,`correctCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getText());
        final String _tmp = __converters.fromStringList(entity.getOptions());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getCorrectAnswerIndex());
        final String _tmp_1 = __converters.fromCategory(entity.getCategory());
        statement.bindString(5, _tmp_1);
        final String _tmp_2 = __converters.fromDifficulty(entity.getDifficulty());
        statement.bindString(6, _tmp_2);
        statement.bindLong(7, entity.getAskedCount());
        statement.bindLong(8, entity.getCorrectCount());
      }
    };
    this.__preparedStmtOfRecordCorrectAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE questions SET askedCount = askedCount + 1, correctCount = correctCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRecordWrongAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE questions SET askedCount = askedCount + 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<Question> questions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfQuestion.insert(questions);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void recordCorrectAnswer(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRecordCorrectAnswer.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfRecordCorrectAnswer.release(_stmt);
    }
  }

  @Override
  public void recordWrongAnswer(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRecordWrongAnswer.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfRecordWrongAnswer.release(_stmt);
    }
  }

  @Override
  public List<Question> getAllQuestions() {
    final String _sql = "SELECT * FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
      final int _cursorIndexOfCorrectAnswerIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswerIndex");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
      final int _cursorIndexOfAskedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "askedCount");
      final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
      final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Question _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpText;
        _tmpText = _cursor.getString(_cursorIndexOfText);
        final List<String> _tmpOptions;
        final String _tmp;
        _tmp = _cursor.getString(_cursorIndexOfOptions);
        _tmpOptions = __converters.toStringList(_tmp);
        final int _tmpCorrectAnswerIndex;
        _tmpCorrectAnswerIndex = _cursor.getInt(_cursorIndexOfCorrectAnswerIndex);
        final QuestionCategory _tmpCategory;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
        _tmpCategory = __converters.toCategory(_tmp_1);
        final QuestionDifficulty _tmpDifficulty;
        final String _tmp_2;
        _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
        _tmpDifficulty = __converters.toDifficulty(_tmp_2);
        final int _tmpAskedCount;
        _tmpAskedCount = _cursor.getInt(_cursorIndexOfAskedCount);
        final int _tmpCorrectCount;
        _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
        _item = new Question(_tmpId,_tmpText,_tmpOptions,_tmpCorrectAnswerIndex,_tmpCategory,_tmpDifficulty,_tmpAskedCount,_tmpCorrectCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Question> getQuestionsByCategory(final QuestionCategory category) {
    final String _sql = "SELECT * FROM questions WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromCategory(category);
    _statement.bindString(_argIndex, _tmp);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
      final int _cursorIndexOfCorrectAnswerIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswerIndex");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
      final int _cursorIndexOfAskedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "askedCount");
      final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
      final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Question _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpText;
        _tmpText = _cursor.getString(_cursorIndexOfText);
        final List<String> _tmpOptions;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfOptions);
        _tmpOptions = __converters.toStringList(_tmp_1);
        final int _tmpCorrectAnswerIndex;
        _tmpCorrectAnswerIndex = _cursor.getInt(_cursorIndexOfCorrectAnswerIndex);
        final QuestionCategory _tmpCategory;
        final String _tmp_2;
        _tmp_2 = _cursor.getString(_cursorIndexOfCategory);
        _tmpCategory = __converters.toCategory(_tmp_2);
        final QuestionDifficulty _tmpDifficulty;
        final String _tmp_3;
        _tmp_3 = _cursor.getString(_cursorIndexOfDifficulty);
        _tmpDifficulty = __converters.toDifficulty(_tmp_3);
        final int _tmpAskedCount;
        _tmpAskedCount = _cursor.getInt(_cursorIndexOfAskedCount);
        final int _tmpCorrectCount;
        _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
        _item = new Question(_tmpId,_tmpText,_tmpOptions,_tmpCorrectAnswerIndex,_tmpCategory,_tmpDifficulty,_tmpAskedCount,_tmpCorrectCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Question> getRandomQuestions(final int count) {
    final String _sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, count);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
      final int _cursorIndexOfCorrectAnswerIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswerIndex");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
      final int _cursorIndexOfAskedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "askedCount");
      final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
      final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Question _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpText;
        _tmpText = _cursor.getString(_cursorIndexOfText);
        final List<String> _tmpOptions;
        final String _tmp;
        _tmp = _cursor.getString(_cursorIndexOfOptions);
        _tmpOptions = __converters.toStringList(_tmp);
        final int _tmpCorrectAnswerIndex;
        _tmpCorrectAnswerIndex = _cursor.getInt(_cursorIndexOfCorrectAnswerIndex);
        final QuestionCategory _tmpCategory;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
        _tmpCategory = __converters.toCategory(_tmp_1);
        final QuestionDifficulty _tmpDifficulty;
        final String _tmp_2;
        _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
        _tmpDifficulty = __converters.toDifficulty(_tmp_2);
        final int _tmpAskedCount;
        _tmpAskedCount = _cursor.getInt(_cursorIndexOfAskedCount);
        final int _tmpCorrectCount;
        _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
        _item = new Question(_tmpId,_tmpText,_tmpOptions,_tmpCorrectAnswerIndex,_tmpCategory,_tmpDifficulty,_tmpAskedCount,_tmpCorrectCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getQuestionCount() {
    final String _sql = "SELECT count(*) FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Question> getDifficultRandomQuestions(final int count) {
    final String _sql = "SELECT * FROM questions WHERE difficulty IN ('ORTA', 'ZOR') ORDER BY RANDOM() LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, count);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
      final int _cursorIndexOfCorrectAnswerIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswerIndex");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
      final int _cursorIndexOfAskedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "askedCount");
      final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
      final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Question _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpText;
        _tmpText = _cursor.getString(_cursorIndexOfText);
        final List<String> _tmpOptions;
        final String _tmp;
        _tmp = _cursor.getString(_cursorIndexOfOptions);
        _tmpOptions = __converters.toStringList(_tmp);
        final int _tmpCorrectAnswerIndex;
        _tmpCorrectAnswerIndex = _cursor.getInt(_cursorIndexOfCorrectAnswerIndex);
        final QuestionCategory _tmpCategory;
        final String _tmp_1;
        _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
        _tmpCategory = __converters.toCategory(_tmp_1);
        final QuestionDifficulty _tmpDifficulty;
        final String _tmp_2;
        _tmp_2 = _cursor.getString(_cursorIndexOfDifficulty);
        _tmpDifficulty = __converters.toDifficulty(_tmp_2);
        final int _tmpAskedCount;
        _tmpAskedCount = _cursor.getInt(_cursorIndexOfAskedCount);
        final int _tmpCorrectCount;
        _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
        _item = new Question(_tmpId,_tmpText,_tmpOptions,_tmpCorrectAnswerIndex,_tmpCategory,_tmpDifficulty,_tmpAskedCount,_tmpCorrectCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
