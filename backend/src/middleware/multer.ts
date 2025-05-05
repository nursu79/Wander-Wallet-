import multer from "multer";
import path from "path";

const upload = multer({
    storage: multer.diskStorage({
        destination: (req, file, cb) => {
            cb(null, file.fieldname == "tripImage" ? "public/tripImages" : "public/userAvatars");
        },
        filename: (req, file, cb) => {
            const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1E9);
            const fileExtension = path.extname(file.originalname);

            cb(null, file.fieldname + "-" + uniqueSuffix + (fileExtension || ".png"));
        }
    }),

    fileFilter: (req, file, cb) => {
        if (file.mimetype.startsWith("image")) {
            return cb(null, true);
        } else {
            cb(Error("Error: File upload only supports images"));
        }
    },

    limits: {
        fileSize: 1024 * 1024 * 5 // 5 MB
    }
});

export default upload;
