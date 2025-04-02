import { Zendesk } from '@codext/capacitor-zendesk';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    Zendesk.echo({ value: inputValue })
}
