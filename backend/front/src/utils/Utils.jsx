// Дополнительный класс, позволяющий работать с пользователем
class Utils {
    saveUser(user) {
        localStorage.setItem('user', JSON.stringify(user))
    }

    removeUser() {
        localStorage.removeItem('user')
    }

    // Метод позволяет получить пользовательский токен
    // Возвращает так, как это написано в заголовках Https
    getToken() {
        let user = JSON.parse(localStorage.getItem('user'))
        return "Bearer " + user.token;
    }

    getUserName() {
        let user = JSON.parse(localStorage.getItem('user'))
        return user && user.login;
    }

    getUser() {
        return JSON.parse(localStorage.getItem('user'))
    }
}

export default new Utils()