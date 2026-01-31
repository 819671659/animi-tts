import asyncio
import edge_tts

async def test():
    voices = await edge_tts.VoicesManager.create()
    voice = voices.find(ShortName="zh-CN-XiaoxiaoNeural")
    if voice:
        print("Voice found")
    else:
        print("Voice NOT found")
    
    communicate = edge_tts.Communicate("Hello", "zh-CN-XiaoxiaoNeural")
    try:
        await communicate.save("test_output.mp3")
        print("Successfully saved audio")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    asyncio.run(test())
