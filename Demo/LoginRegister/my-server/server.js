const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");
const multer = require('multer');
const path = require("path");
const fs = require("fs");
const bcrypt = require("bcrypt");

const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());
app.use("/uploads", express.static(path.join(__dirname, "uploads"))); 

// MySQL Veritabanına Bağlantı
const db = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "veritabanı şifresi",
    database: "user_db"
});

db.connect((err) => {
    if (err) {
        console.error("MySQL bağlantı hatası: " + err);
    } else {
        console.log("MySQL bağlantısı başarılı!");
    }
});



// Yüklenen dosyalar için klasör oluştur
const uploadDir = path.join(__dirname, "uploads");
if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir);
}

// Multer ile dosya yükleme ayarları
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, "uploads/");
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1E9);
        cb(null, file.fieldname + "-" + uniqueSuffix + path.extname(file.originalname));
    }
});

const upload = multer({
    storage: storage,
    limits: { fileSize: 10 * 1024 * 1024 }, // 10 MB
    // Dosya alan adı (field name) burada ayarlanabilir
    fileFilter: (req, file, cb) => {
        if (file.fieldname !== "profile_image" && file.fieldname !== "document" && file.fieldname !== "vehicleReg") {
            return cb(new Error('Unexpected field'), false);  // MulterError: Unexpected field hatası
        }
        cb(null, true);
    }
});

// Kullanıcı kayıt işlemi (profil resmi ve belge ile birlikte)
app.post("/register", upload.fields([
    { name: 'profile_image', maxCount: 1 },
    { name: 'document', maxCount: 1 },
    { name: 'vehicleReg', maxCount: 1 }
]), (req, res) => {
    const { name, surname, trid, phone, address, mail, passwd } = JSON.parse(req.body.user);

    const profileImage = req.files['profile_image']?.[0]?.filename;
    const document = req.files['document']?.[0]?.filename;
    const vehicleReg = req.files['vehicleReg']?.[0]?.filename;

    const sql = "INSERT INTO users (trid, name, surname, phone, address, mail, passwd, profile_image, document, vehicleReg) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    db.query(sql, [trid, name, surname, phone, address, mail, passwd, profileImage, document, vehicleReg], (err, result) => {
        if (err) {
            console.error("MySQL hata: ", err);
            return res.status(500).json({ message: "Veritabanı hatası" });
        }
        res.status(201).json({ message: "Kullanıcı başarıyla kaydedildi!" });
    });
});

// Kullanıcı giriş işlemi
app.post("/login", (req, res) => {
    const { phone, passwd } = req.body;

    const sql = "SELECT * FROM users WHERE phone = ?";
    db.query(sql, [phone], async (err, result) => {
        if (err) {
            return res.status(500).json({ message: "Veritabanı hatası" });
        }
        if (result.length > 0) {
            const user = result[0];
            const isPasswordValid = await bcrypt.compare(passwd, user.passwd);

            if (isPasswordValid) {
                res.status(200).json({ message: "Giriş başarılı!", user });
            } else {
                res.status(401).json({ message: "Telefon veya şifre hatalı!" });
            }
        } else {
            res.status(401).json({ message: "Telefon veya şifre hatalı!" });
        }
    });
});

app.listen(port, () => {
    console.log(`Server ${port} portunda çalışıyor`);
});
