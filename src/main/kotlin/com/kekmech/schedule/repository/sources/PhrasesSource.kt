package com.kekmech.schedule.repository.sources

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.kekmech.schedule.dto.Formattable
import com.kekmech.schedule.dto.Phrases
import com.kekmech.schedule.repository.DataSource
import java.util.concurrent.TimeUnit

class PhrasesSource : DataSource<Unit, Phrases>() {

    override val cache: Cache<Unit, Phrases> = Caffeine.newBuilder()
        .maximumSize(1)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build()

    override fun getFromRemote(k: Unit): Phrases {
        return Phrases(
            actualForOneWord = mapOf(
                "да" to "пизда",
                listOf("нет", "неа", "ноуп", "не") to "а хули нет то?",
                "дед" to listOf("инсайд", "пердед", "не седой - не дед", "{}, зато в штанах"),
                "кек" to listOf("обкекаться", "было так смешно, что я чуть не обосрался", "чет орнул", "действительно кек"),
                "лол" to listOf("нихуя не лол", "ору", "оч смешно да", "{}, брат"),
                "спасибо" to listOf("спасибо в штаны не положишь", "пожалуйста:)", "в благодарность можешь отправить мне свои штаны"),
                listOf("ахах", "ахаха", "ахахах", "отстой", "смешно", "хз") to listOf("{} у тебя в штанах", "в штанах у тебя {}"),
                listOf("обосраться", "срать") to listOf("никто и не сомневался, что ты можешь {} в штаны", "в штаны твои {}", "штаны снять не забудь"),
                "где" to listOf("могу в рифму сказать", "у тебя в штанах"),
                "?" to listOf("????????", "???", "ыыы??????", "ооее?", "ааааооо???"),
                listOf("что", "чо", "че") to listOf("{} {} {} {} {}", "{} {} {}", "брат, {}, брат"),
                "можно" to listOf("мне кажется не можно", "нельзя", "мне кажется нельзя"),
                "нельзя" to listOf("льзя", "можно))"),
                listOf("привет", "здарова", "здравствуйте", "прив", "здороу", "хай") to listOf("иди нахер", "не пиши сюда"),
                listOf("штаны", "джинсы", "брюки", "лоссины", "носки") to listOf("что ты хотел этим сказать"),
                listOf("бля", "блять", "блядь", "блджад") to listOf("хуле ругаешься", "рот с мылом вымой",
                    "такс, матом здесь ругаться можно только мне", "прекращай ругаться"),
                "сука" to listOf("О, русский литературный пошел", "сука не мат"),
                listOf("сучара", "сучка") to listOf("О, русский литературный пошел", "сука не мат, а значит и {} не мат)))"),
                listOf("пизда", "пиздец", "звездец") to listOf("где {}", "ну и где тут {}", "это не {}", "{} это норма", "брат, {}, брат"),
                "я" to listOf("головка от хуя", "мы", "надеюсь что в штанах")
            ).convert(),
            randomPhrasesNouns = listOf(
                "В штанах твоих {}",
                "В штанах у тебя {}",
                "{} у тебя в штанах"
            ).map(::Formattable),
            randomPhrasesVerbs = listOf(
                "Штаны себе сперва {}",
                "{} в штаны себе",
                "штаны себе {}"
            ).map(::Formattable)
        )
    }

    fun getPhrases() = get(Unit)

    private fun Map<Any, Any>.convert(): Map<String, List<Formattable>> = let { m ->
        val newMap = mutableMapOf<String, List<Formattable>>()
        m.forEach { (k, v) ->
            val keys = if (k is List<*>) k.map { it.toString() } else listOf(k.toString())
            val values = if (v is List<*>) v.map { Formattable(it.toString()) } else listOf(Formattable(v.toString()))
            keys.forEach { key ->
                newMap[key] = values
            }
        }
        newMap
    }
}