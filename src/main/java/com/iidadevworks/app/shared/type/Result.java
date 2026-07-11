package com.iidadevworks.app.shared.type;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 処理の成功(Ok)または失敗(Err)を表す型安全な代数的データ型。
 *
 * @param <T> 成功時の値の型
 * @param <E> 失敗時のエラーの型
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

  // --- ファクトリメソッド ---

  /**
   * 成功値を持つResultインスタンスを生成します。
   *
   * @param <T> 成功時の値の型
   * @param <E> 失敗時のエラーの型
   * @param value 成功値
   * @return 成功値を持つResultインスタンス
   */
  static <T, E> Result<T, E> ok(T value) {
    return new Ok<>(value);
  }

  /**
   * 失敗値を持つResultインスタンスを生成します。
   *
   * @param <T> 成功時の値の型
   * @param <E> 失敗時のエラーの型
   * @param error 失敗値
   * @return 失敗値を持つResultインスタンス
   */
  static <T, E> Result<T, E> err(E error) {
    return new Err<>(error);
  }

  // --- 状態確認 ---

  default boolean isOk() {
    return this instanceof Ok;
  }

  default boolean isErr() {
    return this instanceof Err;
  }

  // --- 値の取得 ---

  T unwrap();

  E unwrapErr();

  T orElse(T fallback);

  T orElseGet(Function<? super E, ? extends T> fallbackMapper);

  // --- 変換・チェイン (Monadic Operations) ---

  <U> Result<U, E> map(Function<? super T, ? extends U> mapper);

  <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper);

  <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper);

  // --- 畳み込み / 分岐処理 ---

  <R> R fold(
      Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure);

  // --- 副作用 ---

  Result<T, E> ifOk(Consumer<? super T> action);

  Result<T, E> ifErr(Consumer<? super E> action);

  // --- Optional 相互変換 ---

  default Optional<T> toOptional() {
    return fold(t -> Optional.of(t), e -> Optional.empty());
  }

  default Optional<E> errorToOptional() {
    return fold(t -> Optional.empty(), e -> Optional.of(e));
  }

  // --- 実体定義 (Ok / Err) ---

  /**
   * 成功値を表すResultの実装。
   *
   * @param value 成功値
   */
  record Ok<T, E>(T value) implements Result<T, E> {
    public Ok {
      Objects.requireNonNull(value, "Ok value must not be null");
    }

    @Override
    public T unwrap() {
      return value;
    }

    @Override
    public E unwrapErr() {
      throw new IllegalStateException("Called unwrapErr on an Ok value: " + value);
    }

    @Override
    public T orElse(T fallback) {
      return value;
    }

    @Override
    public T orElseGet(Function<? super E, ? extends T> fallbackMapper) {
      return value;
    }

    @Override
    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper, "mapper must not be null");
      U mapped = Objects.requireNonNull(mapper.apply(value), "mapper returned null");
      return Result.ok(mapped);
    }

    @Override
    public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
      Objects.requireNonNull(mapper, "mapper must not be null");
      return Result.<T, F>ok(value);
    }

    @Override
    public <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
      Objects.requireNonNull(mapper, "mapper must not be null");
      return Objects.requireNonNull(mapper.apply(value), "mapper returned null");
    }

    @Override
    public <R> R fold(
        Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
      return onSuccess.apply(value);
    }

    @Override
    public Result<T, E> ifOk(Consumer<? super T> action) {
      Objects.requireNonNull(action, "action must not be null");
      action.accept(value);
      return this;
    }

    @Override
    public Result<T, E> ifErr(Consumer<? super E> action) {
      return this;
    }
  }

  /**
   * 失敗値を表すResultの実装。
   *
   * @param error 失敗値
   */
  record Err<T, E>(E error) implements Result<T, E> {
    public Err {
      Objects.requireNonNull(error, "Err error must not be null");
    }

    @Override
    public T unwrap() {
      throw new IllegalStateException("Called unwrap on an Err value: " + error);
    }

    @Override
    public E unwrapErr() {
      return error;
    }

    @Override
    public T orElse(T fallback) {
      return fallback;
    }

    @Override
    public T orElseGet(Function<? super E, ? extends T> fallbackMapper) {
      Objects.requireNonNull(fallbackMapper, "fallbackMapper must not be null");
      return Objects.requireNonNull(fallbackMapper.apply(error), "fallbackMapper returned null");
    }

    @Override
    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper, "mapper must not be null");
      return Result.<U, E>err(error);
    }

    @Override
    public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
      Objects.requireNonNull(mapper, "mapper must not be null");
      F mapped = Objects.requireNonNull(mapper.apply(error), "mapper returned null");
      return Result.err(mapped);
    }

    @Override
    public <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
      return Result.<U, E>err(error);
    }

    @Override
    public <R> R fold(
        Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
      return onFailure.apply(error);
    }

    @Override
    public Result<T, E> ifOk(Consumer<? super T> action) {
      return this;
    }

    @Override
    public Result<T, E> ifErr(Consumer<? super E> action) {
      action.accept(error);
      return this;
    }
  }
}
