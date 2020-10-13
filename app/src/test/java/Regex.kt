import org.junit.Test

class Regex {

    @Test
    fun test1() {
        var str = "/sss//dfdf"
        var regex = "[/]$"

        val replace = str.dropLastWhile { it == '/' }

        print(replace)
    }
}