import sys
from io import StringIO

def main(code):
    # Capture stdout
    old_stdout = sys.stdout
    redirected_output = sys.stdout = StringIO()
    
    try:
        # Execute the code
        exec(code)
        sys.stdout = old_stdout
        return redirected_output.getvalue()
    except Exception as e:
        sys.stdout = old_stdout
        return str(e)

def run_with_input(code, input_data):
    # Capture stdout and provide input
    old_stdout = sys.stdout
    old_stdin = sys.stdin
    redirected_output = sys.stdout = StringIO()
    sys.stdin = StringIO(input_data)
    
    try:
        # Execute the code
        exec(code)
        sys.stdout = old_stdout
        sys.stdin = old_stdin
        return redirected_output.getvalue()
    except Exception as e:
        sys.stdout = old_stdout
        sys.stdin = old_stdin
        return str(e)

def run_method(code, method_name, inputs):
    # Create namespace for execution
    namespace = {}
    try:
        # Execute the code in the namespace
        exec(code, namespace)
        
        # Get the method from namespace
        if method_name not in namespace:
            return f"Error: Method '{method_name}' not found"
        
        method = namespace[method_name]
        
        # Convert Java ArrayList to Python list and process inputs
        processed_inputs = []
        if hasattr(inputs, 'toArray'):  # Check if it's a Java ArrayList
            inputs = list(inputs.toArray())  # Convert Java ArrayList to Python list
            
        for input_str in inputs:
            try:
                # First try to convert to int or float directly
                try:
                    if '.' in str(input_str):
                        processed_input = float(input_str)
                    else:
                        processed_input = int(input_str)
                    processed_inputs.append(processed_input)
                    continue
                except (ValueError, TypeError):
                    pass
                
                # If not a simple number, try to evaluate as Python literal
                processed_input = eval(str(input_str))
                if isinstance(processed_input, list):
                    processed_input = list(processed_input)
                processed_inputs.append(processed_input)
            except:
                # If all else fails, keep as string
                processed_inputs.append(str(input_str))
            
        # Call the method with the inputs
        result = method(*processed_inputs)
        return str(result)
    except Exception as e:
        return f"Error: {str(e)}"
