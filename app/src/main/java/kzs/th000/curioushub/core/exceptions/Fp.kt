package kzs.th000.curioushub.core.exceptions

import arrow.core.Either

typealias AppEither<T> = Either<AppException, T>
