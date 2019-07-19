package tofu.concurrent

import cats.Traverse
import cats.effect.Concurrent
import cats.effect.concurrent.Semaphore
import cats.syntax.flatMap._
import cats.syntax.functor._
import tofu.syntax.paralleled._
import tofu.parallel.Paralleled

object ConcurrentOps {
  implicit class TraverseOps[T[_], A](val ta: T[A]) extends AnyVal {
    def limitedTraverse[F[_], B](batchSize: Int)(f: A => F[B])(implicit T: Traverse[T], F: Concurrent[F], P: Paralleled[F]): F[T[B]] =
      for {
        semaphore <- Semaphore[F](batchSize.toLong)
        result <- ta.parTraverse(value => semaphore.withPermit(f(value)))
      } yield result
  }
}