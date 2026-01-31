#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Edge TTS 语音生成脚本
通过命令行参数接收文本和语音配置，生成音频文件

依赖: pip install edge-tts
"""

import edge_tts
import asyncio
import argparse
import sys


async def generate_tts(text, voice, rate, pitch, output):
    """
    生成TTS音频
    
    Args:
        text: 要转换的文本
        voice: 语音角色名称
        rate: 语速（如 +0%）
        pitch: 音调（如 +0Hz）
        output: 输出文件路径
    """
    try:
        communicate = edge_tts.Communicate(
            text, 
            voice,
            rate=rate,
            pitch=pitch
        )
        await communicate.save(output)
        print(f"语音生成成功: {output}")
        
    except Exception as e:
        print(f"语音生成失败: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Edge TTS 语音生成')
    parser.add_argument("--text", required=True, help="要转换的文本")
    parser.add_argument("--voice", required=True, help="语音角色名称")
    parser.add_argument("--rate", default="+0%", help="语速")
    parser.add_argument("--pitch", default="+0Hz", help="音调")
    parser.add_argument("--output", required=True, help="输出文件路径")
    
    args = parser.parse_args()
    
    # 运行异步任务
    asyncio.run(generate_tts(
        args.text, 
        args.voice, 
        args.rate, 
        args.pitch, 
        args.output
    ))
