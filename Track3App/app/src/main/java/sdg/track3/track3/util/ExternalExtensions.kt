package sdg.track3.track3.util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author <@Po10cio> on 11/19/17 for Track3App
 *
 */
fun Disposable.disposeBy(container: CompositeDisposable, disposable: Disposable) {
    container.add(disposable)
}