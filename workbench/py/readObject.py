# coding=utf-8
import re
__metaclass__ = type

class Obj:
    def __init__(self, str):
        pattern = re.compile(r'class (\w+)\{')
        match = pattern.match(str)
        self.name = match.group()
