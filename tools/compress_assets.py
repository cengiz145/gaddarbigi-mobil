import os
import subprocess

def compress_audio(directory):
    for filename in os.listdir(directory):
        if filename.endswith(".mp3"):
            filepath = os.path.join(directory, filename)
            temp_path = os.path.join(directory, "temp_" + filename)
            
            print(f"Compressing {filename}...")
            # 64k bitrate, mono channel (sufficient for quiz background music)
            # -y overwrites output
            cmd = ["ffmpeg", "-i", filepath, "-ac", "1", "-b:a", "64k", "-y", temp_path]
            
            try:
                subprocess.run(cmd, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
                os.replace(temp_path, filepath)
                print(f"Compressed: {filename}")
            except subprocess.CalledProcessError as e:
                print(f"Error compressing {filename}: {e}")
                if os.path.exists(temp_path):
                    os.remove(temp_path)

if __name__ == "__main__":
    # App resource directory
    res_raw_dir = "app/src/main/res/raw"
    if os.path.exists(res_raw_dir):
        compress_audio(res_raw_dir)
        print("Compression completed.")
    else:
        print(f"Directory not found: {res_raw_dir}")
