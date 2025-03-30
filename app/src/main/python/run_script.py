def execute_code(user_code):
    try:
        exec_globals = {}
        exec(user_code, exec_globals)
        return "Code executed successfully!"
    except Exception as e:
        return str(e)
