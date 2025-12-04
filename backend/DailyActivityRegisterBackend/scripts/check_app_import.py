import logging
import sys

logging.basicConfig(stream=sys.stdout, level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('import_check')

logger.info('Starting import check')
try:
    try:
        import main
    except ModuleNotFoundError:
        # Try to load `main.py` directly from repository root
        import importlib.util
        import os
        main_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'main.py'))
        spec = importlib.util.spec_from_file_location('main', main_path)
        main = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(main)
    logger.info('Imported `main` successfully')
    # Print a summary of the FastAPI app if present
    try:
        app = getattr(main, 'app', None)
        if app is not None:
            routes = [r.path for r in app.routes]
            logger.info(f'FastAPI app found with routes: {routes}')
        else:
            logger.warning('No `app` attribute found in `main`')
    except Exception as e:
        logger.error('Error while inspecting `main.app`: %s', e)
except Exception as e:
    logger.exception('Failed to import `main`: %s', e)
    raise
