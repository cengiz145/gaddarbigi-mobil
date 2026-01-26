package com.example.gaddarquiz.feature.tower

object TowerStory {
    const val INTRO_TITLE = "CEHALET KULESİ"
    const val INTRO_TEXT = """
        Dünya artık eskisi gibi değil.
        Büyük Aptallık Savaşı'ndan sonra geriye sadece toz, duman ve bu Kule kaldı.
        
        İnsanlık bildiği her şeyi unuttu. Tarih silindi, bilim yasaklandı.
        Sen, geriye kalan son "Bilge Adayı"sın.
        
        Kule'nin tepesinde "Hakikat Kristali"nin saklandığı söylenir.
        Ona ulaşabilirsen, dünyayı yeniden aydınlatabilirsin.
        
        Ama dikkat et... Kule, cehaletin bekçileriyle dolu.
        Yanlış bir cevap, sadece puanını değil, ruhunu da götürür.
        
        Hazır mısın?
    """

    fun getFloorTitle(floor: Int): String {
        return when (floor) {
            in 1..2 -> "Zindan Katları"
            in 3..4 -> "Unutulmuş Kütüphane"
            in 5..6 -> "Yobazlar Meclisi"
            in 7..8 -> "Karanlık Laboratuvar"
            in 9..10 -> "Hakikat Zirvesi"
            else -> "Hakikat Zirvesi"
        }
    }

    fun getBossName(floor: Int): String {
        return when (floor) {
            5 -> "SADO BEY"
            10 -> "KEL CENGİZ"
            else -> "BEKÇİ"
        }
    }

    fun getBossQuote(floor: Int): String {
        return when (floor) {
            5 -> "Benim mahalleme giremezsin! Burası Sado Bey'in çöplüğü!"
            10 -> "Saçlarım dökülmüş olabilir ama zekam sapasağlam! Benim Krallığımda cehalet kutsaldır!"
            else -> "Kimsin sen?"
        }
    }

    fun getBossVictoryQuote(floor: Int): String {
        return when (floor) {
            5 -> "Ah! Tespihim koptu... Bu iş burada bitmedi aslanım!"
            10 -> "Tacım düştü... Ama Kel Cengiz asla unutmaz! Geri döneceğim!"
            else -> "Nasıl yendin?"
        }
    }

    fun getBossDefeatQuote(floor: Int): String {
        return when (floor) {
            5 -> "Hahaha! Git kumda oyna çocuk! Sado Bey affetmez!"
            10 -> "Gördün mü? Parlak kafam gözünü kamaştırdı! Zayıfsın!"
            else -> "Kayboldun."
        }
    }

    fun getIgnoranceQuote(): String {
        val quotes = listOf(
            "\"Cehalet, Tanrı'nın laneti; bilgi, kanat takıp göklere yükselmektir.\" - Shakespeare",
            "\"Gerçek bilgi, cehaletini bilmektir.\" - Sokrates",
            "\"En büyük düşmanımız bilgisizlik değil, bildiğimizi sanmaktır.\"",
            "\"Cahil kimsenin yanında kitap gibi sessiz ol.\"",
            "\"Cehalet ateş gibidir, yaklaştıkça yakar.\"",
            "\"Karanlığı lanetlemektense, bir mum yak.\"",
            "\"Bilgi güçtür, cehalet ise sadece acı çektirir.\"",
            "\"Bu kulede yankılanan tek ses, boş zihinlerin uğultusudur.\"",
            "\"Gözlerin açık ama zihnin kapalıysa, ışığın sana faydası olmaz.\"",
            "\"Düşünmek zor iştir, bu yüzden çoğu insan yargılamayı seçer.\"",
            "\"Bilgi paylaştıkça çoğalır, cehalet ise paylaştıkça derinleşir.\"",
            "\"En büyük cehalet, bilmediğini bilmemektir.\"",
            "\"Kule'nin her katı, zihnindeki bir zinciri kırmak içindir.\"",
            "\"Gerçek bilge, her zaman öğrenci kalandır.\"",
            "\"Cahil ile dost olma; o sana iyilik yapayım derken kötülük eder.\"",
            "\"Zihin fukara olunca, akıl ukala olurmuş. Sakin ol evlat.\"",
            "\"Okumayı bilmeyen değil, öğrendiklerini sorgulamayan cahildir.\"",
            "\"Bu merdivenler sadece yukarı çıkmaz, seni kendine de götürür.\"",
            "\"Işık arıyorsan, önce içindeki karanlığı kabul etmelisin.\"",
            "\"Sado Bey'in tespihi varsa, senin de aklın var. Unutma!\"",
            "\"Kel Cengiz'in parıltısı zekasından değil, kibre olan aşkındandır.\"",
            "\"Bilgi seni özgür kılar, korku ise bu Kule'ye hapseder.\""
        )
        return quotes.random()
    }
    data class JournalEntry(val author: String, val text: String, val title: String)

    fun getJournalEntry(floor: Int): JournalEntry? {
        return when (floor) {
             3 -> JournalEntry(
                 author = "Gaddar Batu", 
                 title = "Yırtık Bir Sayfa",
                 text = "Sado Bey... Eskiden sadece mahallenin abisiydi. Ama bu kule onu değiştirdi. Artık sadece tespihi değil, insanların kaderini de çekiyor.\n\nDikkat et, soruları TERSTEN sorarak kafanı karıştırmayı sever. Gözünü dört aç."
             )
             7 -> JournalEntry(
                 author = "Kadircan", 
                 title = "Kanlı Bir Not",
                 text = "Kel Cengiz'in kellesini alacaktım ama... Adamın kafası o kadar parlak ki gözlerim kamaştı.\n\nDikkat et, SÜREYLE oynuyor. Seni acele ettirip hata yaptırır. Sakin kalmazsan patlarsın."
             )
             9 -> JournalEntry(
                 author = "Hamza", 
                 title = "Son Mektup",
                 text = "Sona yaklaştım... Ama pilim bitmek üzere. Cengiz'in odasından garip tik-tak sesleri geliyor.\n\nBombaya dikkat et kardeşim. Ben başaramadım, sen başar. Emanetimi yerde bırakma."
             )
             else -> null
        }
    }
}
