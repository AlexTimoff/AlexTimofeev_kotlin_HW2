import java.awt.SystemColor.menu
import java.lang.System.exit
import java.util.regex.Pattern
import kotlin.system.exitProcess

/*
Домашнее задание №2

1. Создайте иерархию sealed классов, которые представляют собой команды. В корне иерархии интерфейс Command.
2. В каждом классе иерархии должна быть функция isValid(): Boolean, которая возвращает true, если команда введена с корректными аргументами.
Проверку телефона и email нужно перенести в эту функцию.
3. Напишите функцию readCommand(): Command, которая читает команду из текстового ввода, распознаёт её и возвращает один из классов-наследников Command,
соответствующий введённой команде.
4. Создайте data класс Person, который представляет собой запись о человеке. Этот класс должен содержать поля:
 name – имя человека
 phone – номер телефона
 email – адрес электронной почты
5. Добавьте новую команду show, которая выводит последнее значение, введённой с помощью команды add. Для этого значение должно быть сохранено в переменную типа Person.
Если на момент выполнения команды show не было ничего введено, нужно вывести на экран сообщение “Not initialized”.
6. Функция main должна выглядеть следующем образом. Для каждой команды от пользователя:
Читаем команду с помощью функции readCommand
Выводим на экран получившийся экземпляр Command
Если isValid для команды возвращает false, выводим help. Если true, обрабатываем команду внутри when.
 */
fun main() {
    println("Введите команду соответсвующую команду или <help> для вывода списка команд на экран ")
    while (true) {
        val command: Command = readCommand()
        if (command.isValid()) {
            println(command)
            command.execute()
        } else {
            error()
            Command.Help()
        }
    }
}


data class Person(
    var name: String,
    var phone: String?=null,
    var email: String?=null
) {
    override fun toString(): String {
        return "Пользователь ${this.name}: " +
                "номер телефона ${this.phone}," +
                "электронная почта ${this.email}"
    }
}

var user: Person? = null

sealed interface AllCommand{
    fun isValid(): Boolean
    fun execute()
}

//Объявляем sealed-класс Command, реализующий интерфес AllCommand. Внутри создаем соответсвующие классы комманд.
sealed class Command: AllCommand {

    class Help : Command() {
        override fun isValid(): Boolean {
            return true
        }

        override fun execute() {
            println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            println("Команда <help> выводит справочную инфформацию")
            println(
                "В рамках команды <add User phone UserPhone> пользователю добавляется номер телефона." +
                        " User - имя пользователя - любое слово. Номер телефона может содержать знак +, затем идут цифры."
            )
            println(
                "в рамках команды <add User email UserEmail> добавляется пользователю электронная почта." +
                        " Электронный адрес должен содержать три последовательности букв, разделённых символами @ и точкой."
            )
            println("Команда <show> выводит последнее значение, введенное спомощью команды add")
            println("Команда <exit> обеспечивает выход из приложения.")
            println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        }

        override fun toString(): String {
            return "Вывод справочной информации о работе приложения"
        }

    }

    class Exit : Command() {
        override fun isValid(): Boolean {
            return true
        }

        override fun execute() {
            println("До свидания!")
            exitProcess(0)
        }

        override fun toString(): String {
            return "Введена команда <exit>"
        }
    }

    class Show : Command() {
        override fun isValid(): Boolean {
            return true
        }

        override fun execute() {
            if (user == null) {
                println("Not initialized")
            } else {
                println(user)
            }
        }

        override fun toString(): String {
            return "Введена команда <show>"
        }
    }


    // Создаем класс добавления номера телефона с проверкой
    class AddPhone(val name: String, val phone: String) : Command() {
        val phonePattern1 = Regex("[+]+\\d+")
        val phonePattern2 = Regex("\\d+")
        override fun isValid(): Boolean {
            return phone.matches(phonePattern1) || phone.matches(phonePattern2)
        }

        override fun execute() {
            user = Person(name = name, phone = phone)
        }

        override fun toString(): String {
            return "Введена команда добавления нового пользователя ${name} с номером телефона ${phone}"
        }
    }

    // Создаем класс добавления электроннйо почты с проверкой
    class AddEmail(val name: String, val email: String) : Command() {
        val emailPattern = Regex("[a-zA-z0-9]+@[a-zA-z0-9]+[.]([a-zA-z0-9]{2,4})")
        override fun isValid(): Boolean {
            return email.matches(emailPattern)
        }

        override fun execute() {
            user = Person(name = name, email = email)
        }

        override fun toString(): String {
            return "Введена команда добавления нового пользователя ${name} с электронной почтой ${email}"
        }
    }

}

fun readCommand(): Command {
    print (">>>>")
    var userInput: String = ""
    userInput = readLine().toString()
    val words: List <String> = userInput.split(' ')
    return when (words[0]){
        "add"->{
            if(words.size==4 &&words[2].contains("phone")){
                Command.AddPhone(words[1],words[3])
            }
            else if (words.size==4 &&words[2].contains("email")) {
                Command.AddEmail(words[1], words[3])
            }
            else{
                error()
                return Command.Help()
            }
        }
        "help"->{
            if(words.size==1){
                Command.Help()
            }
            else{
                error()
                return Command.Help()
            }
        }

        "exit"->{
            if(words.size==1){
                Command.Exit()
            }
            else{
                error()
                return Command.Help()
            }
        }
        "show"->{
            if(words.size==1){
                Command.Show()
            }
            else{
                error()
                return Command.Help()
            }
        }
        else->{
            error()
            return Command.Help()
        }
    }
}


fun error(){
    println("Введена некорректная команда! Попробуйте снова!")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
}



