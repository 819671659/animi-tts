import argparse
import sys
import os
import time

def generate_voice_clone(text, ref_audio_path, output_path):
    """
    Attempt to generate audio using Coqui TTS (XTTS v2) or F5-TTS.
    """
    print(f"Starting legacy voice cloning process...")
    print(f"Text: {text}")
    print(f"Reference Audio: {ref_audio_path}")
    print(f"Output Path: {output_path}")

    # 检查参考音频是否存在
    if not os.path.exists(ref_audio_path):
        print(f"Error: Reference audio file not found at {ref_audio_path}")
        sys.exit(1)

    try:
        # Try importing Coqui TTS
        from TTS.api import TTS
        import torch

        device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"Loading XTTS model on {device}...")
        
        # Environment variable to auto-agree to the Coqui license
        os.environ["COQUI_TOS_AGREED"] = "1"
        
        # Init TTS with the target model name
        try:
            tts = TTS("tts_models/multilingual/multi-dataset/xtts_v2").to(device)
        except Exception as model_err:
            print(f"Error initializing XTTS model: {model_err}")
            # Try to show more detail if it's a specific missing lib
            import traceback
            traceback.print_exc()
            sys.exit(1)

        print("Generating audio...")
        # Run TTS
        tts.tts_to_file(
            text=text,
            speaker_wav=ref_audio_path,
            language="zh-cn", # Force Chinese for this system context
            file_path=output_path
        )
        
        print("Generation successful!")

    except ImportError as e:
        print(f"CRITICAL ERROR: A required Python library is missing: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
        
    except Exception as e:
        print(f"An error occurred during generation: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Voice Cloning Script')
    parser.add_argument('--text', required=True, help='Text to speak')
    parser.add_argument('--ref_audio', required=True, help='Path to reference audio file')
    parser.add_argument('--output', required=True, help='Path to save output audio')
    
    args = parser.parse_args()
    
    start_time = time.time()
    generate_voice_clone(args.text, args.ref_audio, args.output)
    print(f"Total execution time: {time.time() - start_time:.2f}s")
