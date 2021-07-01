package pl.skaucieuropy.rozliczwyjazd.utils

class ToastMessage<T> private constructor(val content: T) {
    companion object {
        fun from(message: String): ToastMessage<String> {
            return ToastMessage(message)
        }

        fun from(messageResId: Int): ToastMessage<Int> {
            return ToastMessage(messageResId)
        }
    }
}
