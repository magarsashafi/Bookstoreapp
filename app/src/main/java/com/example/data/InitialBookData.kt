package com.example.data

object InitialBookData {
    fun getSampleBooks(): List<BookEntity> = listOf(
        BookEntity(
            id = "book_1",
            title = "Hawwii",
            author = "Dhibbaa Toleeraa",
            category = "Asoosama",
            description = "Asoosama beekamaa jaalala, obsa, fi qabsoo jireenya dargaggoota Oromoo mul'isu. Hawwiin abjuu ishee dhugoomsuuf qormaata hedduu keessa dabarti.",
            coverColorHex = "#9E2A2B",
            pagesCount = 148,
            priceEtb = 0.0, // Free / Tola
            rating = 4.9f,
            reviewsCount = 380,
            isDownloaded = true,
            fileSizeMb = 4.8,
            isPurchased = true,
            isFavorite = true,
            isUserListed = false,
            currentPage = 3,
            lastReadTimestamp = System.currentTimeMillis() - 3600000,
            content = """
# Boqonnaa 1: Ganama Barii fi Abjuu Haaraa

Bariin barii ta'ee aduun bakkalcha wajjin yeroo baatu, baadiyyaa Oromiyaa keessatti sagaleen simbirrootaa fi bulluqqiin loonii qilleensa qulqulluu sana faaya. Hawwiin borumtaa isheef karoora guddaa qabdi turte. 

Mana barnootaa sadarkaa lammaffaa xumuruuf qormaatni guddaan fuuldura ishee jira. Haati ishee Aadde Tolashii akkuma aadaa Oromootti ittoo dammaa fi aannan loonii dhiheessitee akkana jette: "Intala koo, hawwiin kee fi barnootni kee ifa borii maatii keenyaati."

Hawwiin boorsaa ishee qabattee karaa dheeraa gara magaalaatti deemuuf kaate. Qilleensi laafaan rifeensa ishee sochoosaa ture. Isheenis yaada keessa lixxee abjuu ishee itti yaaddi: "Guyyaa tokko ogeessa fayyaa ta'ee ummata koo gargaaruun qaba."

---

# Boqonnaa 2: Imala Gara Magaalaa Guddoo

Gannoota muraasa booda, Hawwiin qormaata biyyoolessaa qabxii olaanaadhaan darbitee Yuunivarsiitii Finfinneetti ramadamte. Magaalaa guddoo seentee yeroo jalqabaaf gamoowwan dhedheeroo fi konkolaataawwan baay'ee argite naate.

Garuu onneen ishee jabaadha. Kutaa barnootaa keessatti hiriyoota haaraa kanneen akka Caalaa fi Obsaa waliin wal barte. Caalaan dargaggoo aadaa fi seenaa Oromootti boonuu fi walaloo barreessu ture. Hawwii waliin barumsa irratti wal gargaaruu eegalan.

Caalaan gaaf tokko akkana jedheen: "Hawwii, barumsi beekumsa qofa miti, hiree uummata keenyaaf tajaajila kennuuf bu'uura cimaadha."

---

# Boqonnaa 3: Qormaata Jireenyaa fi Abdii

Waggaa lammaffaa irratti abbaan Hawwii Obbo Badhaasaan dhukkubsatanii hospitaala ciisan. Maatiin qarshii yaalaaf ta'u dhabanii rakkatan. Hawwiin barnoota dhiistee hojii barbaaduuf murteessite.

Garuu hiriyyoonni ishee fi barsiisonni ishee wal ta'anii gargaarsa qarshii walitti qaban. Caalaanis kitaaba walaloo isaa gurguree deeggarsa kenneef. Kun immoo Hawwiidhaaf humna haaraa fi jaalala dhugaa uummata ishee barsiise.

---

# Boqonnaa 4: Wabiilee Jaalalaa

Obsi fi ciminni Hawwii firii buusuu eegale. Hospitaala keessatti shaakala fayyaa jalqabde. Guyyaa tokko dhukkubsataa ulfaataa tokko lubbuu baastee gammadde. 

Galgala sana Caalaan dirree yuunivarsiitii keessatti Hawwii argee walaloo isheef barreesse dubbiseef:
"Simbirroo samii gubbaa
Ija kee keessa jirti nagaan
Ati dachee kootiif ifaa
Abdii fi burqaa jaalalaa..."

Hawwiinis seeqaa: "Jaalalli kee fi gargaarsi kee imala kana na dandeesise," jetteen.

---

# Boqonnaa 5: Injifannoo fi Boqonnaa Xumuraa

Guyyaan eebbifamuu ga'e. Hawwiin uffata eebbaa (gown) uffattee badhaasa olaanaa fudhatte. Haati fi abbaan ishee gammachuudhaan imimmaan dhangalaasan. 

Hawwiin ganda dhaloota isheetti deebitee kilinika jalqabaa banuudhaan haadholii fi daa'imman tajaajiluu jalqabde. Abjuun ishee dhugaa ta'e; seenaan ishees dhaloota haaraadhaaf fakkeenya ta'ee hafe.
            """.trimIndent()
        ),
        BookEntity(
            id = "book_2",
            title = "Gadaa Melbaa: Oromia",
            author = "Gadaa Melbaa",
            category = "Seenaa",
            description = "Kitaaba hundee seenaa Oromoo, heera fi dimokiraasii Sirna Gadaa, fi aadaa Oromoo bal'inaan ibsu. Dhaloota Oromoof madda beekumsaa isa guddaadha.",
            coverColorHex = "#1E3A8A",
            pagesCount = 220,
            priceEtb = 150.0,
            rating = 5.0f,
            reviewsCount = 540,
            isDownloaded = false,
            fileSizeMb = 6.2,
            isPurchased = false,
            isFavorite = true,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = 0L,
            content = """
# Boqonnaa 1: Hundee fi Seenaa Saba Oromoo

Sabni Oromoo uummata durii fi madda dachee Afrikaa Bahaa keessatti seenaa fi aadaa mataa isaa qabaatee jiraatudha. Bal'inni dachee Oromiyaa fi baay'inni uummata isaa qabeenya beekamaadha.

Maddi seenaa Oromoo Odaa fi malkaa, tulluu fi tabba Oromiyaa keessatti akka lafaatti bu'uura godhateera. Oromoon uumaa fi uumama walitti qabee kabajaan ilaala. Waaqa tokkicha amana, Safuu fi seera eega.

---

# Boqonnaa 2: Sirna Gadaa fi Qoodiinsa Teessumaa

Sirni Gadaa sirna dimokiraasii fi bulchiinsaa dhaloota keessatti waggaa saddeet saddeetiin aangoon nagaan wal harkaa fuudhamudha. 

Sadarkaaleen Gadaa:
1. Dabballee (Waggaa 0 - 8)
2. Foollee (Waggaa 8 - 16)
3. Qondaala (Waggaa 16 - 24)
4. Kuusaa (Waggaa 24 - 32)
5. Raaba Doorii (Waggaa 32 - 40)
6. Gadaa (Waggaa 40 - 48 - Yeroo Aangoo fi Bulchiinsaa)

---

# Boqonnaa 3: Moggaasa fi Caasaa Bulchiinsaa

Abbaan Gadaa hooggana olaanaa yoo ta'u, kora Chaffee irratti mariidhaan seera tumu. Haati Manqoo fi Siinqeen mirga dubartootaa eegsisuuf meeshaa aadaa isa cimaadha. Dubartiin Oromoo Siinqee qabattee wayita baatu, dhiirri kamiyyuu seera fi safuu kabajee dhaabbata.

---

# Boqonnaa 4: Sirna Haqaa fi Safuu Oromoo

Haqni Oromoo keessatti lafarratti hin dhiitamu. Dhugaan Waaqaa fi lafaa wal qabatti jedhamee amanama. Jaarsummaan fi gumiin biyyaa rakkoolee uummataa nagaan furuuf gahee adda duree qaba.
            """.trimIndent()
        ),
        BookEntity(
            id = "book_3",
            title = "Kudhaama Seenaa",
            author = "Dr. Haile Fida",
            category = "Seenaa",
            description = "Qorannoo fi barreeffama afaanii, aadaa fi siyaas-diinagdee uummata Oromoo irratti xiyyeeffatu. Qubee fi barreeffama Afaan Oromootiif bu'uura cimaa kaa'e.",
            coverColorHex = "#065F46",
            pagesCount = 185,
            priceEtb = 120.0,
            rating = 4.8f,
            reviewsCount = 210,
            isDownloaded = false,
            fileSizeMb = 5.1,
            isPurchased = false,
            isFavorite = false,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = 0L,
            content = """
# Boqonnaa 1: Eenyummaa fi Qubee Afaan Oromoo

Afaan tokko afaan barreeffamaa ta'uuf carraaqqii fi beekumsa qorannoo guddaa gaafata. Afaan Oromoo afaan bal'aa fi mi'aawaa ummatni miliyoona hedduun dubbatudha.

Qubee Laatiinii fayyadamuun sagalee Afaan Oromootiif baay'ee mijataa ta'uu isaa qorattoonni mirkaneessaniiru. Sagalee dubbachiiftuu gabaabduu fi dheertuu addaan baasuuf qubeen qooda ol'aanaa qaba.

---

# Boqonnaa 2: Barreeffama fi Qorannoo Aadaa

Aadaan Oromoo aadaa afaaniin (oral tradition) dhalootaa dhalootatti darbaa ture yoo ta'u, barreeffamatti jijjiiruun seenaa dacheerra tursiisuuf murteessaa ture. Eebba, geerarsa, mirriga, fi mammaaksa walitti qabuun seenaa dhalootaaf dabarsuuf hojjetameera.
            """.trimIndent()
        ),
        BookEntity(
            id = "book_4",
            title = "Walaloo Jaalalaa fi Bilisummaa",
            author = "Luba Gadaa",
            category = "Walaloo",
            description = "Kuusaa walaloowwan garaa namaa kakaasan, kanneen jaalala dhugaa, miidhagina Oromiyaa, fi abdii egeree sabaa faarsan.",
            coverColorHex = "#7C2D12",
            pagesCount = 96,
            priceEtb = 0.0, // Free
            rating = 4.7f,
            reviewsCount = 150,
            isDownloaded = true,
            fileSizeMb = 2.1,
            isPurchased = true,
            isFavorite = true,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = System.currentTimeMillis() - 86400000,
            content = """
# Kutaa 1: Miira Onnee fi Jaalala Biyyaa

Mukeen Oromiyaa fi gabbina lafaa,
Malkaa laga Awaashii fi qabbana Tulluu Daalattii,
Ija kee keessatti arge ifa barii,
Biyya koo Oromiyaa, onnee koo keessaa hin baatu.

Jaalalli kee akka burqaa haroo,
Yoomiyyuu hin gogu hin dhabu marroo!

---

# Kutaa 2: Finfinnee fi Tulluu Cuqqaalaa

Finfinneen handhuura dachee,
Gadaan kan keeti kan dhaloota kochee.
Hundumtuu sirraa bare miidhagina aadaa,
Haqni kee birmaduu haa tahu yeroo hunda.
            """.trimIndent()
        ),
        BookEntity(
            id = "book_5",
            title = "Ogummaa Daldalaa fi Karoora Jireenyaa",
            author = "Dachasaa Roobaa",
            category = "Ogummaa",
            description = "Qajeelfama qabatamaa namoonni hojii daldalaa eegaluu, bu'aa argachuu, fi diinagdee isaanii ammayyeessuu danda'an of keessaa qaba.",
            coverColorHex = "#1F2937",
            pagesCount = 160,
            priceEtb = 180.0,
            rating = 4.9f,
            reviewsCount = 290,
            isDownloaded = false,
            fileSizeMb = 4.4,
            isPurchased = false,
            isFavorite = false,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = 0L,
            content = """
# Boqonnaa 1: Hubannoo Qarshii fi Qusannaa

Qarshiin meeshaa tajaajilaati malee bu'uura jireenyaa qofaa miti. Galii argattu keessaa dhibbeentaa 20% (digdama) yoo qusatte, egeree keetiif wabii cimaa ijaarratta.

Qusannaan callisanii liqimsuu miti; qarshii sana iddoo bu'aa fidu irratti invastii gochuudha.

---

# Boqonnaa 2: Daldala Eegaluu fi Karoora Hojii

Daldala eegaluuf qarshii guddaa qabaachuun dirqama miti. Rakkina naannoo kee jiru baruu fi furmaata kennuu danda'uudha daldalli dhugaa. 

Karoorri daldalaa qabxiiwwan afur qabaachuu qaba:
1. Maamiltoota kee adda baasuu (Target Customers)
2. Qulqullina oomishaa ykn tajaajilaa (Value Proposition)
3. Baasii fi galii tilmaamuu (Financial Projection)
4. Beeksisa fi dhiheessa ammayyaa (Marketing Strategy)
            """.trimIndent()
        ),
        BookEntity(
            id = "book_6",
            title = "Dhiiga Boorana",
            author = "Mahdi Hamid Muudee",
            category = "Asoosama",
            description = "Asoosama seena qabeessa jireenya Booranaa, ardaa jilaa, waraana fi nagaa, fi jaalala aadaa ibsu.",
            coverColorHex = "#831843",
            pagesCount = 210,
            priceEtb = 140.0,
            rating = 4.8f,
            reviewsCount = 180,
            isDownloaded = false,
            fileSizeMb = 5.8,
            isPurchased = false,
            isFavorite = false,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = 0L,
            content = """
# Boqonnaa 1: Madda Haroo fi Ardaa Jilaa

Ardaan Liiban fi Haroon Daawwee yeroo bonaa dheebuu loonii baasuuf iddoo qulqulluudha. Jaarsoliin Gadaa yaa'anii eebba godhu. 

Dargaggoon Liiban jedhamu gaachana qabatee loon tiksaa ture. Guyyaa tokko intala uffata aadaa faayamte argee onneen isaa rukutuu jalqabde...
            """.trimIndent()
        ),
        BookEntity(
            id = "book_7",
            title = "Barnoota Qubee fi Seerluga Afaan Oromoo",
            author = "Koree Qorannoo Afaanii",
            category = "Barnoota",
            description = "Barattootaa fi barsiisotaaf qajeelfama guutuu seerluga, himootaa, fi qubee Afaan Oromoo ammayyaa.",
            coverColorHex = "#047857",
            pagesCount = 130,
            priceEtb = 0.0, // Free / Tola
            rating = 4.9f,
            reviewsCount = 420,
            isDownloaded = true,
            fileSizeMb = 3.2,
            isPurchased = true,
            isFavorite = false,
            isUserListed = false,
            currentPage = 1,
            lastReadTimestamp = 0L,
            content = """
# Boqonnaa 1: Sagaleewwan fi Dubbachiiftuulee

Afaan Oromoo keessatti qubeewwan dubbachiiftuu shantu jiru: a, e, i, o, u.
Kanneen dheeratan immoo qubee dachaa ta'u: aa, ee, ii, oo, uu.

Sagaleeleen dachaas akka ch, dh, ny, ph, sh, ts jiru. Kanneen sirnaan fayyadamuun hiika jechootaa jijjiira. Fakkeenyaaf: 'Lafa' (earth) fi 'Laafaa' (soft).
            """.trimIndent()
        )
    )
}
