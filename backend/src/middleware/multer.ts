import multer from "multer";
import path from "path";

const upload = multer({
    storage: multer.diskStorage({
        destination: (req, file, cb) => {
            cb(null, "public/")
        },
        filename: (req, file, cb) => {
            const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1E9);
            const fileExtension = path.extname(file.originalname);

            cb(null, file.fieldname + "-" + uniqueSuffix + fileExtension);
        }
    }),

    fileFilter: (req, file, cb) => {
        const filetypes = /jpeg|jpg|png|gif/;
        const mimetype = filetypes.test(file.mimetype);
        const extname = filetypes.test(path.extname(file.originalname).toLowerCase());

        if (mimetype && extname) {
            return cb(null, true);
        } else {
            cb(Error("Error: File upload only supports the following filetypes - " + filetypes));
        }
    },

    limits: {
        fileSize: 1024 * 1024 * 5 // 5 MB
    }
});

export default upload;
