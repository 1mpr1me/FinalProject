import sys
import io

def main(CodeAreaData):
    # Store the original stdout
    old_stdout = sys.stdout
    
    # Create and redirect to buffer
    output_buffer = io.StringIO()
    sys.stdout = output_buffer
    
    try:
        # Print the code we're about to execute
        sys.stderr.write(f"Executing code: {CodeAreaData}\n")
        
        # Execute the code in a local namespace
        local_vars = {}
        exec(CodeAreaData, {}, local_vars)
        
        # Get output before closing buffer
        result = output_buffer.getvalue()
        sys.stderr.write(f"Output captured: {result}\n")
        
        if not result:
            return "No output - Did you forget to use print()?"
        return result
    except Exception as e:
        sys.stderr.write(f"Error occurred: {str(e)}\n")
        return f"Error: {str(e)}"
    finally:
        # Restore stdout and clean up
        sys.stdout = old_stdout
        output_buffer.close()
