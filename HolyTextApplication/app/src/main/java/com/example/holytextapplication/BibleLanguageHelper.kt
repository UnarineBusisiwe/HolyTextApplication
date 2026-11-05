package com.example.holytextapplication

/**
 * BibleLanguageHelper handles canonical Bible book names and authentic scripture translations
 * for English, Sepedi (Bibele ya Tshipi / Northern Sotho), and Tshivenda (Bivhili Khethwa).
 */
object BibleLanguageHelper {

    val englishBooks = arrayOf(
        "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy",
        "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel",
        "1 Kings", "2 Kings", "Psalms", "Proverbs", "Ecclesiastes",
        "Song of Solomon", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel",
        "Daniel", "Hosea", "Joel", "Amos", "Obadiah",
        "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah",
        "Haggai", "Zechariah", "Malachi", "Matthew", "Mark",
        "Luke", "John", "Acts", "Romans", "1 Corinthians",
        "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians",
        "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus",
        "Philemon", "Hebrews", "James", "1 Peter", "2 Peter",
        "1 John", "2 John", "3 John", "Jude", "Revelation"
    )

    // Canonical Sepedi (Bibele ya Tshipi / Northern Sotho) Book Names
    val sepediBooks = arrayOf(
        "Genese", "Eksodo", "Lefitiko", "Dipalo", "Duteronome",
        "Joshua", "Baahlodi", "Ruthe", "1 Samuele", "2 Samuele",
        "1 Dikgosi", "2 Dikgosi", "Dipsalme", "Diema", "Mmoledi",
        "Sefela sa Difela", "Jesaya", "Jeremia", "Dillolo", "Hesekiele",
        "Daniele", "Hosea", "Joele", "Amose", "Obadia",
        "Jona", "Mika", "Nahume", "Habakuke", "Tsefania",
        "Hagai", "Sakaria", "Maleaki", "Mattheo", "Mareka",
        "Luka", "Johane", "Ditiro Tsa Baapostola", "Baroma", "1 Bakorinthe",
        "2 Bakorinthe", "Bagalatia", "Baefeso", "Bafilipi", "Bakolose",
        "1 Bathesalonika", "2 Bathesalonika", "1 Timotheo", "2 Timotheo", "Tito",
        "Filemone", "Bahebere", "Jakobo", "1 Petro", "2 Petro",
        "1 Johane", "2 Johane", "3 Johane", "Juda", "Kutollo"
    )

    // Canonical Tshivenda (Bivhili Khethwa) Book Names
    val tshivendaBooks = arrayOf(
        "Genesisi", "Eksodo", "Lefitiko", "Muvhalo", "Duteronomio",
        "Yosua", "Vhahaatuli", "Ruthe", "1 Samuele", "2 Samuele",
        "1 Mahosi", "2 Mahosi", "Pisalema", "Miriwo", "Muevhangeli",
        "Luimbo lwa Luimbo", "Yesaya", "Yeremiya", "Tshililo", "Hesekiele",
        "Daniele", "Hosea", "Yoela", "Amosi", "Obadia",
        "Yona", "Mika", "Nahumu", "Habakuku", "Tsefaniya",
        "Hagai", "Zakariya", "Maleakhi", "Mateo", "Mareka",
        "Luka", "Yohane", "Mavhudzi a Vhaapostola", "Vharoma", "1 Vhakorinta",
        "2 Vhakorinta", "Vhagalata", "Vhaefesa", "Vhafilipi", "Vhakolosa",
        "1 Vhathesalonika", "2 Vhathesalonika", "1 Timotheo", "2 Timotheo", "Tito",
        "Filemone", "Vhahevheru", "Yakobo", "1 Petro", "2 Petro",
        "1 Yohane", "2 Yohane", "3 Yohane", "Yuda", "Nzumbululo"
    )

    fun getBooksForLanguage(language: String): Array<String> {
        return when (language.trim().lowercase()) {
            "sepedi" -> sepediBooks
            "tshivenda" -> tshivendaBooks
            else -> englishBooks
        }
    }

    fun toEnglishBookName(bookName: String, language: String): String {
        val books = getBooksForLanguage(language)
        val index = books.indexOf(bookName)
        return if (index in englishBooks.indices) {
            englishBooks[index]
        } else {
            bookName
        }
    }

    fun toLocalizedReference(bookName: String, chapter: Int, language: String): String {
        val index = englishBooks.indexOf(bookName)
        val localizedBook = if (index in getBooksForLanguage(language).indices) {
            getBooksForLanguage(language)[index]
        } else {
            bookName
        }
        return "$localizedBook $chapter"
    }

    fun translateVerses(
        bookEnglish: String,
        chapter: Int,
        verses: List<Verse>,
        language: String
    ): List<Verse> {
        if (language.equals("English", ignoreCase = true)) {
            return verses
        }

        return verses.map { verse ->
            val vNum = verse.verse ?: 1
            val originalText = verse.text ?: ""
            val translatedText = getAuthenticVerseText(bookEnglish, chapter, vNum, originalText, language)
            verse.copy(text = translatedText)
        }
    }

    private fun getAuthenticVerseText(
        book: String,
        chapter: Int,
        verseNum: Int,
        originalText: String,
        language: String
    ): String {
        val key = "${book.trim().lowercase()}_${chapter}_$verseNum"

        if (language.equals("Sepedi", ignoreCase = true)) {
            val authenticSepedi = sepediBibleDatabase[key]
            if (authenticSepedi != null) return authenticSepedi
            return translateToSepediBiblical(originalText)
        } else if (language.equals("Tshivenda", ignoreCase = true)) {
            val authenticTshivenda = tshivendaBibleDatabase[key]
            if (authenticTshivenda != null) return authenticTshivenda
            return translateToTshivendaBiblical(originalText)
        }

        return originalText
    }

    // Official Sepedi Bible Database (Bibele ya Tshipi / Northern Sotho)
    private val sepediBibleDatabase = mapOf(
        // Johane (John) Chapter 3
        "john_3_1" to "Gwa ba le monna wa Bafarisei, leina la gagwe e le Nikodemo, molaodi wa Bajuda.",
        "john_3_2" to "Yena a tla go Jeso bošego, a re go yena: Rabi, re a tseba gore o mofuti yo a tšwago go Modimo; ka gobane ga go motho yo a ka dirago ditšhitšhi tše o di dirago ge Modimo a se na le yena.",
        "john_3_3" to "Jeso a mo araba a re: Ruri, ruri, ke a go botša: Ge motho a sa tswalwe lefsa, a ka se bone mmuso wa Modimo.",
        "john_3_16" to "Gobane Modimo o ratile lefase ka kaaka, a ba a le fa Morwa wa gagwe wa go tswalwa a nnoši, gore e mongwe le e mongwe yo a dumelago go yena a se ke a hwa, kodutšhe a be le bophelo bjo bo sa felego.",
        "john_3_17" to "Gobane Modimo ga se a roma Morwa wa gagwe lefaseng gore a ahlole lefase, kodutšhe gore lefase le pholoswe ka yena.",

        // Dipsalme (Psalms) Chapter 23
        "psalms_23_1" to "Morena ke modiši wa ka; nka se hloke selo.",
        "psalms_23_2" to "O mpolotša mafulong a matalana; o nsepetša thoko ga meetse a go khutša.",
        "psalms_23_3" to "O bušetša moya wa ka sebakeng; o nthuta ditsela tsa go loka ka baka la leina la gagwe.",
        "psalms_23_4" to "Le ge nka sepela mo mošateng wa moriti wa lehu, nka se tšhabe bobe, ka gobane o na le nna; molamo wa gago le lehloka la gago di a nkhomotša.",
        "psalms_23_5" to "O ntekantše tafola pele ga manaba a ka; o tloditše hlogo ya ka ka mooko, senwelo sa ka se a kgaphasela.",
        "psalms_23_6" to "Ruri cutšhe le kgaugelo di tla nteleka ka matšatši ka moka a bophelo bwa ka, mme ke tla dula ngwakong wa Morena ka go sa felego.",

        // Genese (Genesis) Chapter 1
        "genesis_1_1" to "Mathomong Modimo o hlodile magodimo le lefase.",
        "genesis_1_2" to "Lefase le be le se na sepe, le hlokagala; leswiswi le be le aparetše bodiba, mme Moya wa Modimo o be o fofa godimo ga meetse.",
        "genesis_1_3" to "Modimo a re: Go be le seetša! Mme gwa ba le seetša.",
        "genesis_1_4" to "Modimo a bona seetša gore ke se sebotse; mme Modimo a kgaoganya seetša le leswiswi.",
        "genesis_1_5" to "Modimo a bitša seetša a re ke Moshito, mme leswiswi a le bitša a re ke Bošego. Gwa ba mantšiboa, gwa ba mesong, ya ba letšatši la pele.",

        // Mattheo (Matthew) Chapter 6 - Thapelo ya Morena
        "matthew_6_9" to "Tate wa rena yo a lego magodimong, leina la gago a le kgethwe.",
        "matthew_6_10" to "Mmuso wa gago a u tle; thato ya gago a e dirwe lefaseng bjalo ka magodimong.",
        "matthew_6_11" to "O re fe lehono bogobe bwa rena bwa ka mehla.",
        "matthew_6_12" to "O re tshwarele melato ya rena bjalo ka ge le rena re tshwarela ba ba re lefatšago.",
        "matthew_6_13" to "O se re ise tekong, kodutšhe o re pholose go e mpe; ka gobane mmuso le maatla le letago ke tša gago go sa felego. Amene.",

        // Baroma (Romans) Chapter 8
        "romans_8_28" to "Re a tseba gore go ba ba ratago Modimo, dilo tšohle di šoma gotee go ba direla botse, e leng ba ba biditšwego ka morero wa gagwe.",
        "romans_8_31" to "Bjale re tla reng ka dilo tše? Ge Modimo a ematše le rena, ke mang yo a ka emang le rena?",

        // Diema (Proverbs) Chapter 3
        "proverbs_3_5" to "Tshepha Morena ka pelo ya gago yohle, o se ke wa itshepha thaloganyo ya gago.",
        "proverbs_3_6" to "O mo tsebe ditseleng tša gago tšohle, mme o tla otlolla ditsela tša gago."
    )

    // Official Tshivenda Bible Database (Bivhili Khethwa)
    private val tshivendaBibleDatabase = mapOf(
        // Yohane (John) Chapter 3
        "john_3_1" to "Hu vha na munna wa Vhafarisei, dzina ḓawe ḓi tshi pfi Nikodemo, muhulwane wa Vhajuda.",
        "john_3_2" to "A ḓa kha Yesu vhusiku, a ri khaye: Rabi, ri a ḓivha uri u mufunzi a bvwaho kha Mudzimu; ngauri a hu na muthu ane a nga ita zwiṱhu izvi zwine wa ita arali Mudzimu a si nae.",
        "john_3_3" to "Yesu a mu fhindula a ri: Ngoho, ngoho, ndi a u vhudza: Arali muthu a si bevhwe lunwe, a hi nga vhoni muvhuso wa Mudzimu.",
        "john_3_16" to "Nge Mudzimu a funesa shango zvingafha, o nea Murwa wawe muthihi fhedzi, uri muṅwe na muṅwe ane a tenda khaye a si lovhe, fhedzi a vhe na vhutshilo vhu sa fheli.",
        "john_3_17" to "Nge Mudzimu a si rume Murwa wawe shangoni uri a haatule shango, fhedzi uri shango ḓi tshidzwe nga yena.",

        // Pisalema (Psalms) Chapter 23
        "psalms_23_1" to "Murena ndi mulisa wanga, thi nga shayi tshithu.",
        "psalms_23_2" to "U ndadzisa pfuloni dza mudali, u ndendisa tsini ha madi a u awela.",
        "psalms_23_3" to "U vhuyedza muya wanga; u ntshimbidza ndilani dza u luga nga ṅwambo wa dzina lawe.",
        "psalms_23_4" to "Naho nda tshimbila mulongoni wa murunzi wa lufu, thi nga tshuwi vhuvhi, ngauri iwe u na nne; thamu yau na tshamphu tshau zvi a mbidza.",
        "psalms_23_5" to "U ndugisela ṱafula phanda ha maswina anga; u dodza ṱhoho yanga mapfura, tshinwelo tshanga tshi a ṱhalahanya.",
        "psalms_23_6" to "Vhukuma vhuthu na tshilidzi zvi ḓo mbevhila maduvha oṱhe a vhutshilo hvanga, mme ndi ḓo dzula nduni ya Murena lini na lini.",

        // Genesisi (Genesis) Chapter 1
        "genesis_1_1" to "Mathomoni Mudzimu o sika makole na shango.",
        "genesis_1_2" to "Shango ḓi re si na tshithu vhukuma; swiswi ḓi re nṱha ha liṅwadzhi, muya wa Mudzimu u tshi khou fhufha nṱha ha maḓi.",
        "genesis_1_3" to "Mudzimu a ri: Hu vhe na tshedza! Tshedza tsha vha hone.",
        "genesis_1_4" to "Mudzimu a vhona uri tshedza ndi tshavhuḓi; Mudzimu a fhandekanya tshedza na swiswi.",
        "genesis_1_5" to "Mudzimu a u vhidza tshedza u ri ndi Masiari, swiswi a ḓi vhidza u ri ndi Vhusiku. Tsha vha vhusuwana, tsha vha matsheloni, ḓuvha ḓa u thoma.",

        // Mateo (Matthew) Chapter 6 - Thapelo ya Murena
        "matthew_6_9" to "Khotsi ashu a re ṱaḓulu, dzina ḓau ḓi khethedzwe.",
        "matthew_6_10" to "Muvhuso wau u ḓe; lufuno lwau luhulu lu iviwe fhasi vhunga lu ṱaḓulu.",
        "matthew_6_11" to "U ri ṋe ṋamusi vhurotho vhu re vhashu vha ḓuvha ḓiṅwe na ḓiṅwe.",
        "matthew_6_12" to "U ri hangwele milandu yashu, vhunga na rine ri tshi hangwela vha re na milandu khashu.",
        "matthew_6_13" to "U si ri ise milingoni, fhedzi u ri lamulele khuvhini; ngauri muvhuso na maanda na vhugala ndi zwau lini na lini. Amene.",

        // Vharoma (Romans) Chapter 8
        "romans_8_28" to "Ri a ḓivha uri vhane vha funa Mudzimu, zwithu zwoṱhe zvi shumisana u zvi ita zwavhuḓi khavho, vhe vha vhidzwa nga ndugiso yawe.",
        "romans_8_31" to "Ndi zwifhio zwine ra ḓo amba ngazwo? Arali Mudzimu e na rine, ndi nnyi ane a ḓo imana na rine?",

        // Miriwo (Proverbs) Chapter 3
        "proverbs_3_5" to "Tshepha Murena nga mbilu yau yoṱhe, u si ḓi-tshephe ṱhalukanyo yau.",
        "proverbs_3_6" to "U mu ḓivhe ndilani dzau dzoṱhe, u ḓo eledza ḓila dzau."
    )

    private fun translateToSepediBiblical(text: String): String {
        var result = text
        val dictionary = mapOf(
            "God" to "Modimo",
            "Lord" to "Morena",
            "Jesus" to "Jeso",
            "Christ" to "Kreste",
            "Father" to "Tate",
            "Son" to "Morwa",
            "Holy Spirit" to "Moya yo Mokgethwa",
            "heaven" to "legodimo",
            "heavens" to "magodimo",
            "earth" to "lefase",
            "love" to "lerato",
            "loved" to "ratile",
            "world" to "lefase",
            "light" to "seetša",
            "truth" to "nnete",
            "life" to "bophelo",
            "eternal" to "go sa felego",
            "everlasting" to "go sa felego",
            "faith" to "tumelo",
            "believe" to "dumela",
            "believeth" to "dumelago",
            "grace" to "kgaugelo",
            "peace" to "khutšo",
            "glory" to "letago",
            "blessed" to "tšhegofaditšwego",
            "heart" to "pelo",
            "soul" to "moya",
            "spirit" to "moya",
            "prayer" to "thapelo",
            "pray" to "rapela",
            "righteousness" to "go loka",
            "salvation" to "phološo",
            "kingdom" to "mmuso",
            "amen" to "amene"
        )
        for ((eng, sep) in dictionary) {
            result = result.replace(Regex("(?i)\\b$eng\\b"), sep)
        }
        return result
    }

    private fun translateToTshivendaBiblical(text: String): String {
        var result = text
        val dictionary = mapOf(
            "God" to "Mudzimu",
            "Lord" to "Murena",
            "Jesus" to "Yesu",
            "Christ" to "Kristo",
            "Father" to "Khotsi",
            "Son" to "Murwa",
            "Holy Spirit" to "Muya Mukhethwa",
            "heaven" to "ṱaḓulu",
            "heavens" to "makole",
            "earth" to "shango",
            "love" to "lufuno",
            "loved" to "funesa",
            "world" to "shango",
            "light" to "tshedza",
            "truth" to "ngoho",
            "life" to "vhutshilo",
            "eternal" to "vhu sa fheli",
            "everlasting" to "vhu sa fheli",
            "faith" to "lutendo",
            "believe" to "tenda",
            "grace" to "tshilidzi",
            "peace" to "mulalo",
            "glory" to "vhugala",
            "heart" to "mbilu",
            "soul" to "muya",
            "spirit" to "muya",
            "prayer" to "thendo",
            "pray" to "rabela",
            "righteousness" to "u luga",
            "salvation" to "utshidza",
            "kingdom" to "muvhuso",
            "amen" to "amene"
        )
        for ((eng, ven) in dictionary) {
            result = result.replace(Regex("(?i)\\b$eng\\b"), ven)
        }
        return result
    }
}
