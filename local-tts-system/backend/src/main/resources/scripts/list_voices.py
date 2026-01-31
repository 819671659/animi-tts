import asyncio
import edge_tts

async def list_voices():
    voices = await edge_tts.VoicesManager.create()
    all_zh = [v for v in voices.find() if v['Locale'].startswith("zh-")]
    for v in all_zh:
        print(f"Locale: {v['Locale']}, Name: {v['ShortName']}, Gender: {v['Gender']}")

if __name__ == "__main__":
    asyncio.run(list_voices())
