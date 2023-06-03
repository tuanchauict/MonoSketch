import hashlib
import os
import shutil
import subprocess
import livereload

ROOT = "."

COMPILE_DIR = f"{ROOT}/build/developmentExecutable"
SERVE_DIR = f"{ROOT}/build/dev"


def compile_code():
    subprocess.run(["sh", "gradlew", "browserDevExeDis"])
    prepare_for_serve()


def prepare_for_serve():
    def dest_file(root: str, file):
        dest_folder = root.replace(COMPILE_DIR, SERVE_DIR)
        return os.path.join(dest_folder, file) if file else dest_folder

    def src_file(root: str, file):
        return os.path.join(root, file)

    def checksum(file_path):
        if not os.path.isfile(file_path):
            return ''
        with open(file_path, "rb") as f:
            return hashlib.md5(f.read()).hexdigest()

    def should_move(src_path: str, src_filename: str):
        if src_path.startswith(f"{COMPILE_DIR}/style"):
            return False
        if src_filename.endswith(".css.map"):
            return src_filename == 'main.css.map'
        src = src_file(src_path, src_filename)
        dest = dest_file(src_path, src_filename)
        return checksum(src) != checksum(dest)

    for root, _, files in os.walk(COMPILE_DIR):
        for file in files:
            if should_move(root, file):
                if not os.path.exists(dest_file(root, "")):
                    os.makedirs(dest_file(root, ""))
                shutil.copyfile(src_file(root, file), dest_file(root, file))


prepare_for_serve()

server = livereload.Server()
server.watch(f"{ROOT}/**/src/main/**/*.kt", compile_code, delay='forever', delay_exe=2.5)
server.watch(f"{ROOT}/**/src/main/**/*.scss", compile_code, delay='forever', delay_exe=2.5)
server.watch(f"{SERVE_DIR}/**/*", lambda x: None)
server.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
server.serve(root=SERVE_DIR, port=8000, open_url_delay=None)
