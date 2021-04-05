package com.kekmech.schedule

import com.kekmech.schedule.dto.VKMessage
import com.kekmech.schedule.helpers.MessageCounter
import com.kekmech.schedule.repository.MainRepository
import com.kekmech.schedule.repository.sources.PhrasesSource
import org.junit.jupiter.api.Test

class MainRepoTest {

    @Test
    fun repoTest() {
        val r = MainRepository(PhrasesSource(), MessageCounter())
        for (i in 1..100) {
            for (j in 1..10) {
                val p = r.getPhrase(VKMessage(0, 0, 0, 0, 0, "сам ты пес$j"))
                if (p != null) println(p)
            }
        }
    }
}